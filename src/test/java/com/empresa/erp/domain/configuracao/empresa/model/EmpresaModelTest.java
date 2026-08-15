package com.empresa.erp.domain.configuracao.empresa.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.old.StatusEnum;

class EmpresaModelTest {

    @Test
    @DisplayName("Deve criar empresa ativa e normalizar nome")
    void deveCriarEmpresaAtivaENormalizarNome() {
        var dados = new EmpresaRecord("  Empresa   Exemplo  ");

        var empresa = new EmpresaModel(dados);

        assertThat(empresa.getNome()).isEqualTo("Empresa Exemplo");
        assertThat(empresa.getStatus()).isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve atualizar e normalizar nome da empresa")
    void deveAtualizarENormalizarNomeDaEmpresa() {
        var empresa = new EmpresaModel(
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
    }

    @Test
    @DisplayName("Deve inativar empresa")
    void deveInativarEmpresa() {
        var empresa = new EmpresaModel(
                new EmpresaRecord("Empresa Exemplo")
        );

        empresa.inativar();

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover empresa registrando auditoria")
    void deveRemoverEmpresaRegistrandoAuditoria() {
        var empresa = new EmpresaModel(
                new EmpresaRecord("Empresa Exemplo")
        );

        empresa.remover(10L);

        assertThat(empresa.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(empresa.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(empresa.getRemovidoEm())
                .isNotNull();
    }
}