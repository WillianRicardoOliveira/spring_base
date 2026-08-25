package com.empresa.erp.core.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.empresa.erp.core.exception.AccessTokenException;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.record.AccessTokenValidadoSecurity;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FilterSecurityTest {

    @Mock
    private TokenSecurity tokenService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private UsuarioSessaoService usuarioSessaoService;

    private FilterSecurity filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        filter = new FilterSecurity(
                tokenService,
                usuarioAutenticadoService,
                usuarioSessaoService
        );
    }

    @Test
    @DisplayName("Deve seguir filtro quando nao houver token")
    void deveSeguirFiltroQuandoNaoHouverToken() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        assertThat(response.getStatus())
                .isEqualTo(200);

        verifyNoInteractions(tokenService);
        verifyNoInteractions(usuarioAutenticadoService);
        verifyNoInteractions(usuarioSessaoService);
    }

    @Test
    @DisplayName(
            "Deve autenticar usuario quando token for valido "
                    + "e sessao estiver ativa"
    )
    void deveAutenticarUsuarioQuandoTokenForValidoESessaoEstiverAtiva()
            throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        request.addHeader(
                "Authorization",
                "Bearer token-valido"
        );

        var accessToken =
                new AccessTokenValidadoSecurity(
                        "admin@futuro.com",
                        "jti-valido"
                );

        var usuario = new UsuarioModel();

        var usuarioAutenticado =
                new UsuarioAutenticado(
                        usuario,
                        List.of()
                );

        when(tokenService.validarAccessToken("token-valido"))
                .thenReturn(accessToken);

        when(usuarioSessaoService.accessTokenEstaAtivo("jti-valido"))
                .thenReturn(true);

        when(usuarioAutenticadoService.buscarPorEmail("admin@futuro.com"))
                .thenReturn(usuarioAutenticado);

        filter.doFilter(request, response, chain);

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertThat(authentication)
                .isNotNull();

        assertThat(authentication.getPrincipal())
                .isEqualTo(usuarioAutenticado);

        assertThat(response.getStatus())
                .isEqualTo(200);

        verify(tokenService)
                .validarAccessToken("token-valido");

        verify(usuarioSessaoService)
                .accessTokenEstaAtivo("jti-valido");

        verify(usuarioAutenticadoService)
                .buscarPorEmail("admin@futuro.com");
    }

    @Test
    @DisplayName(
            "Deve retornar 401 quando token for valido "
                    + "mas usuario nao for encontrado"
    )
    void deveRetornar401QuandoTokenForValidoMasUsuarioNaoForEncontrado()
            throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        request.addHeader(
                "Authorization",
                "Bearer token-valido"
        );

        var accessToken =
                new AccessTokenValidadoSecurity(
                        "admin@futuro.com",
                        "jti-valido"
                );

        when(tokenService.validarAccessToken("token-valido"))
                .thenReturn(accessToken);

        when(usuarioSessaoService.accessTokenEstaAtivo("jti-valido"))
                .thenReturn(true);

        when(usuarioAutenticadoService.buscarPorEmail("admin@futuro.com"))
                .thenReturn(null);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        assertThat(response.getStatus())
                .isEqualTo(401);

        assertThat(response.getContentType())
                .contains(MediaType.APPLICATION_JSON_VALUE);

        assertThat(response.getContentAsString())
                .contains("\"status\":401")
                .contains("\"erro\":\"TOKEN_INVALIDO\"")
                .contains(
                        "\"mensagem\":\"Token invalido ou expirado\""
                );

        verify(tokenService)
                .validarAccessToken("token-valido");

        verify(usuarioSessaoService)
                .accessTokenEstaAtivo("jti-valido");

        verify(usuarioAutenticadoService)
                .buscarPorEmail("admin@futuro.com");
    }

    @Test
    @DisplayName("Deve retornar 401 em JSON quando token for invalido")
    void deveRetornar401EmJsonQuandoTokenForInvalido()
            throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        request.addHeader(
                "Authorization",
                "Bearer token-invalido"
        );

        when(tokenService.validarAccessToken("token-invalido"))
                .thenThrow(
                        new AccessTokenException(
                                "Token invalido"
                        )
                );

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        assertThat(response.getStatus())
                .isEqualTo(401);

        assertThat(response.getContentType())
                .contains(MediaType.APPLICATION_JSON_VALUE);

        assertThat(response.getContentAsString())
                .contains("\"status\":401")
                .contains("\"erro\":\"TOKEN_INVALIDO\"")
                .contains(
                        "\"mensagem\":\"Token invalido ou expirado\""
                );

        verify(tokenService)
                .validarAccessToken("token-invalido");

        verifyNoInteractions(usuarioAutenticadoService);
        verifyNoInteractions(usuarioSessaoService);
    }

    @Test
    @DisplayName(
            "Deve retornar 401 em JSON quando access token "
                    + "estiver revogado"
    )
    void deveRetornar401EmJsonQuandoAccessTokenEstiverRevogado()
            throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        request.addHeader(
                "Authorization",
                "Bearer token-revogado"
        );

        var accessToken =
                new AccessTokenValidadoSecurity(
                        "admin@futuro.com",
                        "jti-revogado"
                );

        when(tokenService.validarAccessToken("token-revogado"))
                .thenReturn(accessToken);

        when(usuarioSessaoService.accessTokenEstaAtivo("jti-revogado"))
                .thenReturn(false);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        assertThat(response.getStatus())
                .isEqualTo(401);

        assertThat(response.getContentType())
                .contains(MediaType.APPLICATION_JSON_VALUE);

        assertThat(response.getContentAsString())
                .contains("\"status\":401")
                .contains("\"erro\":\"TOKEN_INVALIDO\"")
                .contains(
                        "\"mensagem\":\"Token invalido ou expirado\""
                );

        verify(tokenService)
                .validarAccessToken("token-revogado");

        verify(usuarioSessaoService)
                .accessTokenEstaAtivo("jti-revogado");

        verifyNoInteractions(usuarioAutenticadoService);
    }

    @Test
    @DisplayName("Deve ignorar authorization header sem bearer")
    void deveIgnorarAuthorizationHeaderSemBearer()
            throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        request.addHeader(
                "Authorization",
                "Basic abc"
        );

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        assertThat(response.getStatus())
                .isEqualTo(200);

        verifyNoInteractions(tokenService);
        verifyNoInteractions(usuarioAutenticadoService);
        verifyNoInteractions(usuarioSessaoService);
    }
}