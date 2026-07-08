package com.empresa.erp.core.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioLogadoServiceTest {

    private UsuarioLogadoService service;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        service = new UsuarioLogadoService();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve retornar id do usuario autenticado")
    void deveRetornarIdDoUsuarioAutenticado() {
        var usuario = criarUsuario(10L, "usuario@teste.com");
        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());

        var authentication = new UsernamePasswordAuthenticationToken(
                usuarioAutenticado,
                null,
                usuarioAutenticado.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var resultado = service.getId();

        assertThat(resultado).isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve bloquear quando nao houver autenticacao")
    void deveBloquearQuandoNaoHouverAutenticacao() {
        assertThatThrownBy(() -> service.getId())
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario nao autenticado.");
    }

    @Test
    @DisplayName("Deve bloquear quando autenticacao nao estiver autenticada")
    void deveBloquearQuandoAutenticacaoNaoEstiverAutenticada() {
        var authentication = new UsernamePasswordAuthenticationToken("usuario@teste.com", "senha");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> service.getId())
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario nao autenticado.");
    }

    @Test
    @DisplayName("Deve bloquear quando principal for invalido")
    void deveBloquearQuandoPrincipalForInvalido() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "usuario@teste.com",
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThatThrownBy(() -> service.getId())
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario autenticado invalido.");
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}