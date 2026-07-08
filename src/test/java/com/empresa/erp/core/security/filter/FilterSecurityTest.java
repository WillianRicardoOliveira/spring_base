package com.empresa.erp.core.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class FilterSecurityTest {

    private TokenSecurity tokenService;
    private UsuarioAutenticadoService usuarioAutenticadoService;
    private FilterSecurity filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        tokenService = org.mockito.Mockito.mock(TokenSecurity.class);
        usuarioAutenticadoService = org.mockito.Mockito.mock(UsuarioAutenticadoService.class);

        filter = new FilterSecurity(tokenService, usuarioAutenticadoService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir o filtro sem autenticar quando nao houver token")
    void deveSeguirOFiltroSemAutenticarQuandoNaoHouverToken() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
        assertThat(response.getStatus()).isEqualTo(200);

        verifyNoInteractions(tokenService, usuarioAutenticadoService);
    }

    @Test
    @DisplayName("Deve ignorar header Authorization sem prefixo Bearer")
    void deveIgnorarHeaderAuthorizationSemPrefixoBearer() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc123");

        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
        assertThat(response.getStatus()).isEqualTo(200);

        verifyNoInteractions(tokenService, usuarioAutenticadoService);
    }

    @Test
    @DisplayName("Deve autenticar usuario quando token for valido")
    void deveAutenticarUsuarioQuandoTokenForValido() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt-token");

        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        var usuario = criarUsuario(1L, "usuario@teste.com");
        var usuarioAutenticado = new UsuarioAutenticado(
                usuario,
                List.of(new SimpleGrantedAuthority("ACESSO_USUARIO_LISTAR"))
        );

        when(tokenService.getSubject("jwt-token")).thenReturn("usuario@teste.com");
        when(usuarioAutenticadoService.buscarPorEmail("usuario@teste.com")).thenReturn(usuarioAutenticado);

        filter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(usuarioAutenticado);
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ACESSO_USUARIO_LISTAR");

        assertThat(filterChain.getRequest()).isSameAs(request);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(tokenService).getSubject("jwt-token");
        verify(usuarioAutenticadoService).buscarPorEmail("usuario@teste.com");
    }

    @Test
    @DisplayName("Deve seguir o filtro sem autenticar quando usuario nao for encontrado")
    void deveSeguirOFiltroSemAutenticarQuandoUsuarioNaoForEncontrado() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt-token");

        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        when(tokenService.getSubject("jwt-token")).thenReturn("usuario@teste.com");
        when(usuarioAutenticadoService.buscarPorEmail("usuario@teste.com")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isSameAs(request);
        assertThat(response.getStatus()).isEqualTo(200);

        verify(tokenService).getSubject("jwt-token");
        verify(usuarioAutenticadoService).buscarPorEmail("usuario@teste.com");
    }

    @Test
    @DisplayName("Deve retornar 401 quando token for invalido")
    void deveRetornar401QuandoTokenForInvalido() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt-token-invalido");

        var response = new MockHttpServletResponse();
        var filterChain = new MockFilterChain();

        when(tokenService.getSubject("jwt-token-invalido"))
                .thenThrow(new RuntimeException("Token JWT invalido ou expirado"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo("text/plain;charset=UTF-8");
        assertThat(response.getContentAsString()).isEqualTo("Token invalido ou expirado");

        verify(tokenService).getSubject("jwt-token-invalido");
        verifyNoInteractions(usuarioAutenticadoService);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}