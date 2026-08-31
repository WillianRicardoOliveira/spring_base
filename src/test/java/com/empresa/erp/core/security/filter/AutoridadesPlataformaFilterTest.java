package com.empresa.erp.core.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.AutoridadesPlataformaService;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

import jakarta.servlet.FilterChain;

@ExtendWith(MockitoExtension.class)
class AutoridadesPlataformaFilterTest {

    private static final Long ID_USUARIO =
            10L;

    @Mock
    private AutoridadesPlataformaService
            autoridadesPlataformaService;

    @Mock
    private FilterChain filterChain;

    private AutoridadesPlataformaFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        filter =
                new AutoridadesPlataformaFilter(
                        autoridadesPlataformaService
                );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName(
            "Deve continuar sem consultar autoridades quando não houver autenticação"
    )
    void deveContinuarSemConsultarAutoridadesQuandoNaoHouverAutenticacao()
            throws Exception {
        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );

        verifyNoInteractions(
                autoridadesPlataformaService
        );
    }

    @Test
    @DisplayName(
            "Deve continuar sem consultar autoridades quando autenticação não estiver confirmada"
    )
    void deveContinuarSemConsultarAutoridadesQuandoAutenticacaoNaoEstiverConfirmada()
            throws Exception {
        UsuarioAutenticado usuarioAutenticado =
                criarUsuarioAutenticado(
                        List.of()
                );

        var autenticacao =
                new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado,
                        null
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        autenticacao
                );

        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );

        verifyNoInteractions(
                autoridadesPlataformaService
        );
    }

    @Test
    @DisplayName(
            "Deve continuar sem consultar autoridades para principal incompatível"
    )
    void deveContinuarSemConsultarAutoridadesParaPrincipalIncompativel()
            throws Exception {
        var autenticacao =
                new UsernamePasswordAuthenticationToken(
                        "usuario",
                        null,
                        List.of()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        autenticacao
                );

        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );

        verifyNoInteractions(
                autoridadesPlataformaService
        );
    }

    @Test
    @DisplayName(
            "Deve combinar autoridades existentes com autoridades da plataforma"
    )
    void deveCombinarAutoridadesExistentesComAutoridadesDaPlataforma()
            throws Exception {
        UsuarioAutenticado usuarioAutenticado =
                autenticarUsuario(
                        List.of(
                                new SimpleGrantedAuthority(
                                        "AUTORIDADE_TOKEN"
                                )
                        ),
                        "detalhes-autenticacao"
                );

        when(autoridadesPlataformaService.buscar(
                ID_USUARIO
        )).thenReturn(
                List.of(
                        new SimpleGrantedAuthority(
                                "PLATAFORMA_ORGANIZACAO_LISTAR"
                        ),
                        new SimpleGrantedAuthority(
                                "PLATAFORMA_ORGANIZACAO_CRIAR"
                        )
                )
        );

        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var novaAutenticacao =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertThat(novaAutenticacao)
                .isNotNull();

        assertThat(novaAutenticacao)
                .isNotSameAs(
                        usuarioAutenticado
                );

        assertThat(novaAutenticacao.getPrincipal())
                .isSameAs(
                        usuarioAutenticado
                );

        assertThat(novaAutenticacao.getDetails())
                .isEqualTo(
                        "detalhes-autenticacao"
                );

        assertThat(
                novaAutenticacao.getAuthorities()
        )
                .extracting(
                        autoridade ->
                                autoridade.getAuthority()
                )
                .containsExactly(
                        "AUTORIDADE_TOKEN",
                        "PLATAFORMA_ORGANIZACAO_LISTAR",
                        "PLATAFORMA_ORGANIZACAO_CRIAR"
                );

        verify(autoridadesPlataformaService)
                .buscar(ID_USUARIO);

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    @DisplayName(
            "Não deve duplicar autoridade já presente na autenticação"
    )
    void naoDeveDuplicarAutoridadeJaPresenteNaAutenticacao()
            throws Exception {
        autenticarUsuario(
                List.of(
                        new SimpleGrantedAuthority(
                                "PLATAFORMA_ORGANIZACAO_LISTAR"
                        )
                ),
                null
        );

        when(autoridadesPlataformaService.buscar(
                ID_USUARIO
        )).thenReturn(
                List.of(
                        new SimpleGrantedAuthority(
                                "PLATAFORMA_ORGANIZACAO_LISTAR"
                        )
                )
        );

        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var autoridades =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities();

        assertThat(autoridades)
                .extracting(
                        autoridade ->
                                autoridade.getAuthority()
                )
                .containsExactly(
                        "PLATAFORMA_ORGANIZACAO_LISTAR"
                );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    @DisplayName(
            "Deve preservar autoridades existentes quando usuário não possuir acesso à plataforma"
    )
    void devePreservarAutoridadesExistentesQuandoUsuarioNaoPossuirAcessoAPlataforma()
            throws Exception {
        autenticarUsuario(
                List.of(
                        new SimpleGrantedAuthority(
                                "AUTORIDADE_TOKEN"
                        )
                ),
                null
        );

        when(autoridadesPlataformaService.buscar(
                ID_USUARIO
        )).thenReturn(
                List.of()
        );

        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        filter.doFilter(
                request,
                response,
                filterChain
        );

        var autoridades =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities();

        assertThat(autoridades)
                .extracting(
                        autoridade ->
                                autoridade.getAuthority()
                )
                .containsExactly(
                        "AUTORIDADE_TOKEN"
                );

        verify(filterChain)
                .doFilter(
                        request,
                        response
                );
    }

    @Test
    @DisplayName(
            "Não deve continuar a requisição quando carregamento das autoridades falhar"
    )
    void naoDeveContinuarRequisicaoQuandoCarregamentoDasAutoridadesFalhar() {
        autenticarUsuario(
                List.of(),
                null
        );

        when(autoridadesPlataformaService.buscar(
                ID_USUARIO
        )).thenThrow(
                new AccessDeniedException(
                        "Não foi possível carregar as autoridades."
                )
        );

        var request =
                new MockHttpServletRequest();

        var response =
                new MockHttpServletResponse();

        assertThatThrownBy(
                () -> filter.doFilter(
                        request,
                        response,
                        filterChain
                )
        )
                .isInstanceOf(
                        AccessDeniedException.class
                )
                .hasMessage(
                        "Não foi possível carregar as autoridades."
                );

        verifyNoInteractions(filterChain);
    }

    private UsuarioAutenticado autenticarUsuario(
            List<SimpleGrantedAuthority> autoridades,
            Object detalhes
    ) {
        UsuarioAutenticado usuarioAutenticado =
                criarUsuarioAutenticado(
                        autoridades
                );

        var autenticacao =
                new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado,
                        null,
                        autoridades
                );

        autenticacao.setDetails(detalhes);

        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        autenticacao
                );

        return usuarioAutenticado;
    }

    private UsuarioAutenticado criarUsuarioAutenticado(
            List<SimpleGrantedAuthority> autoridades
    ) {
        UsuarioModel usuario =
                new UsuarioModel(
                        ID_USUARIO,
                        "usuario@teste.com",
                        "senha-criptografada",
                        StatusEnum.ATIVO
                );

        return new UsuarioAutenticado(
                usuario,
                autoridades
        );
    }
}