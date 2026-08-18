package com.empresa.erp.core.organizacao.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.organizacao.service.ContextoOrganizacaoService;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;

@MockitoSettings(strictness = Strictness.LENIENT)
class ContextoOrganizacaoFilterTest {

    @Mock
    private ContextoOrganizacaoService
            contextoOrganizacaoService;

    @Mock
    private AccessDeniedHandler
            acessoNegadoHandler;

    @Mock
    private FilterChain filterChain;

    private ContextoOrganizacaoFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        filter = new ContextoOrganizacaoFilter(
                contextoOrganizacaoService,
                acessoNegadoHandler,
                new ObjectMapper()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir sem header de organização")
    void deveSeguirSemHeaderDeOrganizacao()
            throws Exception {
        autenticarUsuario();

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(
                request,
                response
        );

        verifyNoInteractions(
                contextoOrganizacaoService,
                acessoNegadoHandler
        );
    }

    @Test
    @DisplayName("Deve ignorar header sem usuário autenticado")
    void deveIgnorarHeaderSemUsuarioAutenticado()
            throws Exception {
        var request = criarRequestComOrganizacao("20");
        var response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(
                request,
                response
        );

        verifyNoInteractions(
                contextoOrganizacaoService,
                acessoNegadoHandler
        );
    }

    @Test
    @DisplayName("Deve validar organização e continuar")
    void deveValidarOrganizacaoEContinuar()
            throws Exception {
        autenticarUsuario();

        var request = criarRequestComOrganizacao("20");
        var response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(contextoOrganizacaoService)
                .definir(20L);

        verify(filterChain).doFilter(
                request,
                response
        );

        verifyNoInteractions(acessoNegadoHandler);
    }

    @Test
    @DisplayName("Deve aceitar espaços ao redor do id")
    void deveAceitarEspacosAoRedorDoId()
            throws Exception {
        autenticarUsuario();

        var request =
                criarRequestComOrganizacao(" 20 ");
        var response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(contextoOrganizacaoService)
                .definir(20L);

        verify(filterChain).doFilter(
                request,
                response
        );
    }

    @Test
    @DisplayName("Deve retornar 400 para header vazio")
    void deveRetornar400ParaHeaderVazio()
            throws Exception {
        autenticarUsuario();

        var request = criarRequestComOrganizacao("");
        var response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(response.getStatus())
                .isEqualTo(400);

        assertThat(response.getContentType())
                .contains(
                        MediaType.APPLICATION_JSON_VALUE
                );

        assertThat(response.getContentAsString())
                .contains("\"status\":400")
                .contains(
                        "\"erro\":\"REGRA_DE_NEGOCIO\""
                )
                .contains(
                        "\"mensagem\":\"Organizacao invalida.\""
                );

        verifyNoInteractions(
                contextoOrganizacaoService,
                acessoNegadoHandler
        );

        verify(
                filterChain,
                never()
        ).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 400 para header não numérico")
    void deveRetornar400ParaHeaderNaoNumerico()
            throws Exception {
        autenticarUsuario();

        var request =
                criarRequestComOrganizacao("abc");
        var response = new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(response.getStatus())
                .isEqualTo(400);

        assertThat(response.getContentAsString())
                .contains(
                        "\"mensagem\":\"Organizacao invalida.\""
                );

        verifyNoInteractions(
                contextoOrganizacaoService,
                acessoNegadoHandler
        );

        verify(
                filterChain,
                never()
        ).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve retornar 400 para organização inválida")
    void deveRetornar400ParaOrganizacaoInvalida()
            throws Exception {
        autenticarUsuario();

        var request = criarRequestComOrganizacao("0");
        var response = new MockHttpServletResponse();

        var exception = new ValidacaoException(
                "Organizacao invalida."
        );

        org.mockito.Mockito.doThrow(exception)
                .when(contextoOrganizacaoService)
                .definir(0L);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        assertThat(response.getStatus())
                .isEqualTo(400);

        assertThat(response.getContentAsString())
                .contains(
                        "\"mensagem\":\"Organizacao invalida.\""
                );

        verify(
                filterChain,
                never()
        ).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve delegar acesso negado ao handler")
    void deveDelegarAcessoNegadoAoHandler()
            throws Exception {
        autenticarUsuario();

        var request = criarRequestComOrganizacao("20");
        var response = new MockHttpServletResponse();

        var exception = new AccessDeniedException(
                "Acesso negado."
        );

        org.mockito.Mockito.doThrow(exception)
                .when(contextoOrganizacaoService)
                .definir(20L);

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(acessoNegadoHandler).handle(
                request,
                response,
                exception
        );

        verify(
                filterChain,
                never()
        ).doFilter(request, response);
    }

    @Test
    @DisplayName(
            "Deve propagar erro lançado depois "
                    + "da validação organizacional"
    )
    void devePropagarErroLancadoDepoisDaValidacaoOrganizacional()
            throws Exception {
        autenticarUsuario();

        var request = criarRequestComOrganizacao("20");
        var response = new MockHttpServletResponse();

        org.mockito.Mockito.doThrow(
                new ValidacaoException(
                        "Erro do controller."
                )
        ).when(filterChain).doFilter(
                request,
                response
        );

        assertThatThrownBy(() ->
                filter.doFilter(
                        request,
                        response,
                        filterChain
                )
        )
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Erro do controller.");

        verify(contextoOrganizacaoService)
                .definir(20L);
    }

    private MockHttpServletRequest
            criarRequestComOrganizacao(String id) {
        var request = new MockHttpServletRequest();

        request.addHeader(
                ContextoOrganizacaoFilter
                        .HEADER_ORGANIZACAO,
                id
        );

        return request;
    }

    private void autenticarUsuario() {
        var usuarioAutenticado =
                new UsuarioAutenticado(
                        new UsuarioModel(),
                        List.of()
                );

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado,
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }
}