package com.empresa.erp.domain.acesso.perfil.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.old.StatusEnum;

class PerfilModelTest {

    @Test
    @DisplayName("Deve criar perfil ativo")
    void deveCriarPerfilAtivo() {
        var dados = new PerfilRecord("Administrador", "Perfil administrador");

        var perfil = new PerfilModel(dados);

        assertThat(perfil.getNome()).isEqualTo("Administrador");
        assertThat(perfil.getDescricao()).isEqualTo("Perfil administrador");
        assertThat(perfil.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar perfil")
    void deveAtualizarPerfil() {
        var perfil = new PerfilModel(new PerfilRecord("Administrador", "Perfil administrador"));

        perfil.atualizar(new AtualizaPerfilRecord(1L, "Administrador Master", "Perfil atualizado"));

        assertThat(perfil.getNome()).isEqualTo("Administrador Master");
        assertThat(perfil.getDescricao()).isEqualTo("Perfil atualizado");
    }

    @Test
    @DisplayName("Deve inativar perfil")
    void deveInativarPerfil() {
        var perfil = new PerfilModel(new PerfilRecord("Administrador", "Perfil administrador"));

        perfil.inativar();

        assertThat(perfil.getStatus()).isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover perfil registrando auditoria")
    void deveRemoverPerfilRegistrandoAuditoria() {
        var perfil = new PerfilModel(new PerfilRecord("Administrador", "Perfil administrador"));

        perfil.remover(10L);

        assertThat(perfil.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(perfil.getRemovidoPor()).isEqualTo(10L);
        assertThat(perfil.getRemovidoEm()).isNotNull();
    }
}