package com.empresa.erp.domain.usuario.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioModelTest {

    @Test
    @DisplayName("Deve criar usuario ativo com email normalizado")
    void deveCriarUsuarioAtivoComEmailNormalizado() {
        var dados = new UsuarioRecord(" Usuario@Teste.com ", "123456");

        var usuario = new UsuarioModel(dados, "senha-criptografada");

        assertThat(usuario.getEmail()).isEqualTo("usuario@teste.com");
        assertThat(usuario.getSenha()).isEqualTo("senha-criptografada");
        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(usuario.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Deve atualizar email normalizando valor")
    void deveAtualizarEmailNormalizandoValor() {
        var usuario = new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        );

        usuario.atualizar(new AtualizaUsuarioRecord(1L, " Novo@Teste.com "));

        assertThat(usuario.getEmail()).isEqualTo("novo@teste.com");
    }

    @Test
    @DisplayName("Deve atualizar senha")
    void deveAtualizarSenha() {
        var usuario = new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        );

        usuario.atualizarSenha("nova-senha-criptografada");

        assertThat(usuario.getSenha()).isEqualTo("nova-senha-criptografada");
    }

    @Test
    @DisplayName("Deve inativar usuario")
    void deveInativarUsuario() {
        var usuario = new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        );

        usuario.inativar();

        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(usuario.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve remover usuario registrando auditoria")
    void deveRemoverUsuarioRegistrandoAuditoria() {
        var usuario = new UsuarioModel(
                new UsuarioRecord("usuario@teste.com", "123456"),
                "senha-criptografada"
        );

        usuario.remover(10L);

        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(usuario.getRemovidoPor()).isEqualTo(10L);
        assertThat(usuario.getRemovidoEm()).isNotNull();
        assertThat(usuario.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Deve aceitar email nulo na normalizacao interna")
    void deveAceitarEmailNuloNaNormalizacaoInterna() {
        var usuario = new UsuarioModel(
                new UsuarioRecord(null, "123456"),
                "senha-criptografada"
        );

        assertThat(usuario.getEmail()).isNull();
        assertThat(usuario.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }
}