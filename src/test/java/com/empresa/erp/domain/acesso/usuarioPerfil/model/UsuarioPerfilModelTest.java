package com.empresa.erp.domain.acesso.usuarioPerfil.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioPerfilModelTest {

    @Test
    @DisplayName("Deve criar vinculo ativo entre usuario e perfil")
    void deveCriarVinculoAtivoEntreUsuarioEPerfil() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador");

        var usuarioPerfil = new UsuarioPerfilModel(usuario, perfil);

        assertThat(usuarioPerfil.getUsuario()).isEqualTo(usuario);
        assertThat(usuarioPerfil.getPerfil()).isEqualTo(perfil);
        assertThat(usuarioPerfil.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve remover vinculo entre usuario e perfil registrando auditoria")
    void deveRemoverVinculoEntreUsuarioEPerfilRegistrandoAuditoria() {
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var perfil = criarPerfil(2L, "Administrador");
        var usuarioPerfil = new UsuarioPerfilModel(usuario, perfil);

        usuarioPerfil.remover(10L);

        assertThat(usuarioPerfil.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(usuarioPerfil.getRemovidoPor()).isEqualTo(10L);
        assertThat(usuarioPerfil.getRemovidoEm()).isNotNull();
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
}