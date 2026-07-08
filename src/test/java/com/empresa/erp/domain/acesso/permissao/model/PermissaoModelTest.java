package com.empresa.erp.domain.acesso.permissao.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.old.StatusEnum;

class PermissaoModelTest {

    @Test
    @DisplayName("Deve criar permissao ativa")
    void deveCriarPermissaoAtiva() {
        var dados = new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        var permissao = new PermissaoModel(dados);

        assertThat(permissao.getNome()).isEqualTo("Listar usuarios");
        assertThat(permissao.getChave()).isEqualTo("ACESSO_USUARIO_LISTAR");
        assertThat(permissao.getDescricao()).isEqualTo("Permite listar usuarios");
        assertThat(permissao.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar permissao")
    void deveAtualizarPermissao() {
        var permissao = new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        ));

        permissao.atualizar(new AtualizaPermissaoRecord(
                1L,
                "Editar usuarios",
                "ACESSO_USUARIO_EDITAR",
                "Permite editar usuarios"
        ));

        assertThat(permissao.getNome()).isEqualTo("Editar usuarios");
        assertThat(permissao.getChave()).isEqualTo("ACESSO_USUARIO_EDITAR");
        assertThat(permissao.getDescricao()).isEqualTo("Permite editar usuarios");
    }

    @Test
    @DisplayName("Deve inativar permissao")
    void deveInativarPermissao() {
        var permissao = new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        ));

        permissao.inativar();

        assertThat(permissao.getStatus()).isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover permissao registrando auditoria")
    void deveRemoverPermissaoRegistrandoAuditoria() {
        var permissao = new PermissaoModel(new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        ));

        permissao.remover(10L);

        assertThat(permissao.getStatus()).isEqualTo(StatusEnum.REMOVIDO);
        assertThat(permissao.getRemovidoPor()).isEqualTo(10L);
        assertThat(permissao.getRemovidoEm()).isNotNull();
    }
}