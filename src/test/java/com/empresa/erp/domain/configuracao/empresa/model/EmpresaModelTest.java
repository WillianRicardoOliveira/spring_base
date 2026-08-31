package com.empresa.erp.domain.configuracao.empresa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class EmpresaModelTest {

    @Test
    @DisplayName(
            "Deve criar empresa ativa vinculada a organizacao e normalizar nome"
    )
    void deveCriarEmpresaAtivaVinculadaAOrganizacaoENormalizarNome() {
        var organizacao =
                new OrganizacaoModel("Organizacao Exemplo");

        var dados =
                new EmpresaRecord("  Empresa   Exemplo  ");

        var empresa = new EmpresaModel(
                organizacao,
                dados
        );

        assertThat(empresa.getOrganizacao())
                .isSameAs(organizacao);

        assertThat(empresa.getNome())
                .isEqualTo("Empresa Exemplo");

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName(
            "Deve atualizar e normalizar nome sem alterar organizacao"
    )
    void deveAtualizarENormalizarNomeSemAlterarOrganizacao() {
        var organizacao =
                new OrganizacaoModel("Organizacao Exemplo");

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord("Empresa Exemplo")
        );

        empresa.atualizar(
                new AtualizaEmpresaRecord(
                        1L,
                        "  Empresa   Atualizada  "
                )
        );

        assertThat(empresa.getNome())
                .isEqualTo("Empresa Atualizada");

        assertThat(empresa.getOrganizacao())
                .isSameAs(organizacao);
    }

    @Test
    @DisplayName(
            "Deve inativar empresa sem alterar organizacao"
    )
    void deveInativarEmpresaSemAlterarOrganizacao() {
        var organizacao =
                new OrganizacaoModel("Organizacao Exemplo");

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord("Empresa Exemplo")
        );

        empresa.inativar();

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.INATIVO);

        assertThat(empresa.getOrganizacao())
                .isSameAs(organizacao);
    }

    @Test
    @DisplayName(
            "Deve remover empresa registrando auditoria sem alterar organizacao"
    )
    void deveRemoverEmpresaRegistrandoAuditoriaSemAlterarOrganizacao() {
        var organizacao =
                new OrganizacaoModel("Organizacao Exemplo");

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord("Empresa Exemplo")
        );

        empresa.remover(10L);

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(empresa.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(empresa.getRemovidoEm())
                .isNotNull();

        assertThat(empresa.getOrganizacao())
                .isSameAs(organizacao);
    }
}