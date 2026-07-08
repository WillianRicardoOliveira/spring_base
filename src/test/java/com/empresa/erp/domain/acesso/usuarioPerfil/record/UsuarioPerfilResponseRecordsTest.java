package com.empresa.erp.domain.acesso.usuarioPerfil.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioPerfilResponseRecordsTest {

    @Test
    @DisplayName("Deve criar DetalheUsuarioPerfilRecord a partir do model")
    void deveCriarDetalheUsuarioPerfilRecordAPartirDoModel() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador");
        var usuarioPerfil = criarUsuarioPerfil(3L, usuario, perfil);

        var detalhe = new DetalheUsuarioPerfilRecord(usuarioPerfil);

        assertThat(detalhe.id()).isEqualTo(3L);
        assertThat(detalhe.idUsuario()).isEqualTo(1L);
        assertThat(detalhe.usuario()).isEqualTo("usuario@teste.com");
        assertThat(detalhe.idPerfil()).isEqualTo(2L);
        assertThat(detalhe.perfil()).isEqualTo("Administrador");
        assertThat(detalhe.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar ListaUsuarioPerfilRecord a partir do model")
    void deveCriarListaUsuarioPerfilRecordAPartirDoModel() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Financeiro");
        var usuarioPerfil = criarUsuarioPerfil(3L, usuario, perfil);

        var lista = new ListaUsuarioPerfilRecord(usuarioPerfil);

        assertThat(lista.id()).isEqualTo(3L);
        assertThat(lista.idPerfil()).isEqualTo(2L);
        assertThat(lista.perfil()).isEqualTo("Financeiro");
        assertThat(lista.status()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status removido no record de detalhe")
    void deveRefletirStatusRemovidoNoRecordDeDetalhe() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador");
        var usuarioPerfil = criarUsuarioPerfil(3L, usuario, perfil);

        usuarioPerfil.remover(10L);

        var detalhe = new DetalheUsuarioPerfilRecord(usuarioPerfil);

        assertThat(detalhe.status()).isEqualTo(StatusEnum.REMOVIDO);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private PerfilModel criarPerfil(Long id, String nome) {
        var perfil = new PerfilModel(new PerfilRecord(nome, "Perfil " + nome));
        ReflectionTestUtils.setField(perfil, "id", id);
        return perfil;
    }

    private UsuarioPerfilModel criarUsuarioPerfil(Long id, UsuarioModel usuario, PerfilModel perfil) {
        var usuarioPerfil = new UsuarioPerfilModel(usuario, perfil);
        ReflectionTestUtils.setField(usuarioPerfil, "id", id);
        return usuarioPerfil;
    }
}