package com.empresa.erp.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.record.LoginSecurity;
import com.empresa.erp.core.security.record.RefreshTokenSecurity;
import com.empresa.erp.core.security.record.SsoLoginSecurity;
import com.empresa.erp.core.security.record.TokenGeradoSecurity;
import com.empresa.erp.core.security.record.TokenJwtSecurity;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

class AutenticacaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthenticationManager manager;
    private TokenSecurity tokenService;
    private SsoSecurity ssoSecurity;
    private UsuarioSessaoService usuarioSessaoService;

    @BeforeEach
    void setUp() {
        manager = org.mockito.Mockito.mock(AuthenticationManager.class);
        tokenService = org.mockito.Mockito.mock(TokenSecurity.class);
        ssoSecurity = org.mockito.Mockito.mock(SsoSecurity.class);
        usuarioSessaoService = org.mockito.Mockito.mock(UsuarioSessaoService.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AutenticacaoController(
                        manager,
                        tokenService,
                        ssoSecurity,
                        usuarioSessaoService
                ))
                .build();
    }

    @Test
    @DisplayName("Deve efetuar login e retornar token JWT com refresh token")
    void deveEfetuarLoginERetornarTokenJwtComRefreshToken() throws Exception {
        var dados = new LoginSecurity("usuario@teste.com", "123456");
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());
        var authentication = new UsernamePasswordAuthenticationToken(usuarioAutenticado, dados.senha(), List.of());

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.gerarTokenComJti(usuario)).thenReturn(new TokenGeradoSecurity("jwt-token", "jti-token"));
        when(usuarioSessaoService.criarSessao(usuario, "jti-token", "192.168.0.10", "Postman"))
                .thenReturn("refresh-token");

        mockMvc.perform(post("/login")
                        .header("X-Forwarded-For", "192.168.0.10")
                        .header("User-Agent", "Postman")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).gerarTokenComJti(usuario);
        verify(usuarioSessaoService).criarSessao(usuario, "jti-token", "192.168.0.10", "Postman");
    }

    @Test
    @DisplayName("Deve renovar token usando refresh token")
    void deveRenovarTokenUsandoRefreshToken() throws Exception {
        var dados = new RefreshTokenSecurity("refresh-token");

        when(usuarioSessaoService.renovarSessao("refresh-token"))
                .thenReturn(new TokenJwtSecurity("novo-jwt-token", "novo-refresh-token"));

        mockMvc.perform(post("/login/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("novo-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("novo-refresh-token"));

        verify(usuarioSessaoService).renovarSessao("refresh-token");
        verifyNoInteractions(manager, tokenService, ssoSecurity);
    }

    @Test
    @DisplayName("Deve realizar logout e revogar sessao")
    void deveRealizarLogoutERevogarSessao() throws Exception {
        var dados = new RefreshTokenSecurity("refresh-token");

        mockMvc.perform(post("/login/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isNoContent());

        verify(usuarioSessaoService).revogarSessao("refresh-token");
        verifyNoInteractions(manager, tokenService, ssoSecurity);
    }

    @Test
    @DisplayName("Deve retornar 400 ao realizar logout sem refresh token")
    void deveRetornar400AoRealizarLogoutSemRefreshToken() throws Exception {
        var dados = new RefreshTokenSecurity("");

        mockMvc.perform(post("/login/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity, usuarioSessaoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao renovar token sem refresh token")
    void deveRetornar400AoRenovarTokenSemRefreshToken() throws Exception {
        var dados = new RefreshTokenSecurity("");

        mockMvc.perform(post("/login/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity, usuarioSessaoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login com email em branco")
    void deveRetornar400AoEfetuarLoginComEmailEmBranco() throws Exception {
        var dados = new LoginSecurity("", "123456");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity, usuarioSessaoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login com email invalido")
    void deveRetornar400AoEfetuarLoginComEmailInvalido() throws Exception {
        var dados = new LoginSecurity("email-invalido", "123456");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity, usuarioSessaoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login com senha em branco")
    void deveRetornar400AoEfetuarLoginComSenhaEmBranco() throws Exception {
        var dados = new LoginSecurity("usuario@teste.com", "");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity, usuarioSessaoService);
    }

    @Test
    @DisplayName("Deve efetuar login SSO e retornar token JWT")
    void deveEfetuarLoginSsoERetornarTokenJwt() throws Exception {
        var dados = new SsoLoginSecurity("sso-token");

        when(ssoSecurity.autenticar(any(SsoLoginSecurity.class)))
                .thenReturn(new TokenJwtSecurity("jwt-sso-token"));

        mockMvc.perform(post("/login/sso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-sso-token"));

        verify(ssoSecurity).autenticar(any(SsoLoginSecurity.class));
        verifyNoInteractions(manager, tokenService, usuarioSessaoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login SSO sem token")
    void deveRetornar400AoEfetuarLoginSsoSemToken() throws Exception {
        var dados = new SsoLoginSecurity("");

        mockMvc.perform(post("/login/sso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity, usuarioSessaoService);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "Senha@123"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}