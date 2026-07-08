package com.empresa.erp.core.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.SsoAuthenticationException;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.record.SsoLoginSecurity;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

class SsoSecurityTest {

    private ObjectProvider<JwtDecoder> jwtDecoderProvider;
    private JwtDecoder jwtDecoder;
    private UsuarioRepository usuarioRepository;
    private TokenSecurity tokenSecurity;
    private SsoSecurity service;

    @BeforeEach
    void setUp() {
        jwtDecoderProvider = org.mockito.Mockito.mock(ObjectProvider.class);
        jwtDecoder = org.mockito.Mockito.mock(JwtDecoder.class);
        usuarioRepository = org.mockito.Mockito.mock(UsuarioRepository.class);
        tokenSecurity = org.mockito.Mockito.mock(TokenSecurity.class);

        service = new SsoSecurity(
                jwtDecoderProvider,
                usuarioRepository,
                tokenSecurity,
                "email",
                "",
                ""
        );
    }

    @Test
    @DisplayName("Deve autenticar com token SSO valido")
    void deveAutenticarComTokenSsoValido() {
        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of("email", "usuario@teste.com"));
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);
        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com")).thenReturn(usuario);
        when(tokenSecurity.gerarToken(usuario)).thenReturn("jwt-token");

        var resultado = service.autenticar(dados);

        assertThat(resultado.token()).isEqualTo("jwt-token");

        verify(jwtDecoder).decode("sso-token");
        verify(usuarioRepository).findByEmailIgnoreCase("usuario@teste.com");
        verify(tokenSecurity).gerarToken(usuario);
    }

    @Test
    @DisplayName("Deve extrair email de preferred_username quando email nao existir")
    void deveExtrairEmailDePreferredUsernameQuandoEmailNaoExistir() {
        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of("preferred_username", "Usuario@Teste.com"));
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);
        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com")).thenReturn(usuario);
        when(tokenSecurity.gerarToken(usuario)).thenReturn("jwt-token");

        var resultado = service.autenticar(dados);

        assertThat(resultado.token()).isEqualTo("jwt-token");

        verify(usuarioRepository).findByEmailIgnoreCase("usuario@teste.com");
    }

    @Test
    @DisplayName("Deve autenticar quando audience e scope forem validos")
    void deveAutenticarQuandoAudienceEScopeForemValidos() {
        service = new SsoSecurity(
                jwtDecoderProvider,
                usuarioRepository,
                tokenSecurity,
                "email",
                "erp-api",
                "erp.login"
        );

        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of(
                "email", "usuario@teste.com",
                "aud", List.of("erp-api"),
                "scp", "openid profile erp.login"
        ));
        var usuario = criarUsuario(1L, "usuario@teste.com");

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);
        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com")).thenReturn(usuario);
        when(tokenSecurity.gerarToken(usuario)).thenReturn("jwt-token");

        var resultado = service.autenticar(dados);

        assertThat(resultado.token()).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("Deve bloquear token SSO nao informado")
    void deveBloquearTokenSsoNaoInformado() {
        var dados = new SsoLoginSecurity("");

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("Token SSO nao informado");

        verifyNoInteractions(jwtDecoderProvider, usuarioRepository, tokenSecurity);
    }

    @Test
    @DisplayName("Deve bloquear quando SSO nao estiver configurado")
    void deveBloquearQuandoSsoNaoEstiverConfigurado() {
        var dados = new SsoLoginSecurity("sso-token");

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(null);

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("SSO nao configurado");

        verifyNoInteractions(usuarioRepository, tokenSecurity);
    }

    @Test
    @DisplayName("Deve bloquear token SSO invalido ou expirado")
    void deveBloquearTokenSsoInvalidoOuExpirado() {
        var dados = new SsoLoginSecurity("sso-token");

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenThrow(new JwtException("token invalido"));

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("Token SSO invalido ou expirado");

        verifyNoInteractions(usuarioRepository, tokenSecurity);
    }

    @Test
    @DisplayName("Deve bloquear quando email nao existir no token SSO")
    void deveBloquearQuandoEmailNaoExistirNoTokenSso() {
        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of("sub", "123"));

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("E-mail nao encontrado no token SSO");

        verifyNoInteractions(usuarioRepository, tokenSecurity);
    }

    @Test
    @DisplayName("Deve bloquear usuario nao autorizado para SSO")
    void deveBloquearUsuarioNaoAutorizadoParaSso() {
        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of("email", "usuario@teste.com"));

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);
        when(usuarioRepository.findByEmailIgnoreCase("usuario@teste.com")).thenReturn(null);

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("Usuario nao autorizado para SSO");

        verifyNoInteractions(tokenSecurity);
    }

    @Test
    @DisplayName("Deve bloquear token SSO com audience invalida")
    void deveBloquearTokenSsoComAudienceInvalida() {
        service = new SsoSecurity(
                jwtDecoderProvider,
                usuarioRepository,
                tokenSecurity,
                "email",
                "erp-api",
                ""
        );

        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of(
                "email", "usuario@teste.com",
                "aud", List.of("outra-api")
        ));

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("Token SSO nao pertence a esta aplicacao");

        verifyNoInteractions(usuarioRepository, tokenSecurity);
    }

    @Test
    @DisplayName("Deve bloquear token SSO sem scope autorizado")
    void deveBloquearTokenSsoSemScopeAutorizado() {
        service = new SsoSecurity(
                jwtDecoderProvider,
                usuarioRepository,
                tokenSecurity,
                "email",
                "",
                "erp.login"
        );

        var dados = new SsoLoginSecurity("sso-token");
        var jwt = criarJwt(Map.of(
                "email", "usuario@teste.com",
                "scp", "openid profile"
        ));

        when(jwtDecoderProvider.getIfAvailable()).thenReturn(jwtDecoder);
        when(jwtDecoder.decode("sso-token")).thenReturn(jwt);

        assertThatThrownBy(() -> service.autenticar(dados))
                .isInstanceOf(SsoAuthenticationException.class)
                .hasMessage("Token SSO sem permissao para login");

        verifyNoInteractions(usuarioRepository, tokenSecurity);
    }

    private Jwt criarJwt(Map<String, Object> claims) {
        return new Jwt(
                "sso-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}