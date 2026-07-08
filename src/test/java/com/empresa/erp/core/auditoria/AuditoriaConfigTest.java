package com.empresa.erp.core.auditoria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class AuditoriaConfigTest {

    private AuditoriaConfig config;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        config = new AuditoriaConfig();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve retornar id do usuario autenticado como auditor")
    void deveRetornarIdDoUsuarioAutenticadoComoAuditor() {
        var usuario = criarUsuario(10L, "usuario@teste.com");
        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());

        var authentication = new UsernamePasswordAuthenticationToken(
                usuarioAutenticado,
                null,
                usuarioAutenticado.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var auditor = config.auditorProvider().getCurrentAuditor();

        assertThat(auditor).contains(10L);
    }

    @Test
    @DisplayName("Deve retornar auditor vazio quando nao houver autenticacao")
    void deveRetornarAuditorVazioQuandoNaoHouverAutenticacao() {
        var auditor = config.auditorProvider().getCurrentAuditor();

        assertThat(auditor).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar auditor vazio quando autenticacao nao estiver autenticada")
    void deveRetornarAuditorVazioQuandoAutenticacaoNaoEstiverAutenticada() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "usuario@teste.com",
                "senha"
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var auditor = config.auditorProvider().getCurrentAuditor();

        assertThat(auditor).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar auditor vazio quando principal for invalido")
    void deveRetornarAuditorVazioQuandoPrincipalForInvalido() {
        var authentication = new UsernamePasswordAuthenticationToken(
                "usuario@teste.com",
                null,
                List.of()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var auditor = config.auditorProvider().getCurrentAuditor();

        assertThat(auditor).isEmpty();
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}