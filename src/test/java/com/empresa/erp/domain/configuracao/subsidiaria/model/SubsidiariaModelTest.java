package com.empresa.erp.domain.configuracao.subsidiaria.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.AtualizaSubsidiariaRecord;
import com.empresa.erp.domain.old.StatusEnum;

class SubsidiariaModelTest {

    @Test
    @DisplayName("Deve criar subsidiaria ativa")
    void deveCriarSubsidiariaAtiva() {
        var empresa = criarEmpresa();
        var subsidiaria = new SubsidiariaModel(
                empresa,
                "Filial Curitiba"
        );

        assertThat(subsidiaria.getEmpresa())
                .isSameAs(empresa);

        assertThat(subsidiaria.getNome())
                .isEqualTo("Filial Curitiba");

        assertThat(subsidiaria.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve normalizar nome ao criar subsidiaria")
    void deveNormalizarNomeAoCriarSubsidiaria() {
        var subsidiaria = new SubsidiariaModel(
                criarEmpresa(),
                "  Filial   Curitiba  "
        );

        assertThat(subsidiaria.getNome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName("Deve atualizar e normalizar nome")
    void deveAtualizarENormalizarNome() {
        var subsidiaria = new SubsidiariaModel(
                criarEmpresa(),
                "Filial Curitiba"
        );

        subsidiaria.atualizar(
                new AtualizaSubsidiariaRecord(
                        1L,
                        "  Filial   Parana  "
                )
        );

        assertThat(subsidiaria.getNome())
                .isEqualTo("Filial Parana");
    }

    @Test
    @DisplayName("Deve inativar subsidiaria")
    void deveInativarSubsidiaria() {
        var subsidiaria = new SubsidiariaModel(
                criarEmpresa(),
                "Filial Curitiba"
        );

        subsidiaria.inativar();

        assertThat(subsidiaria.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover subsidiaria com auditoria")
    void deveRemoverSubsidiariaComAuditoria() {
        var subsidiaria = new SubsidiariaModel(
                criarEmpresa(),
                "Filial Curitiba"
        );

        subsidiaria.remover(10L);

        assertThat(subsidiaria.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(subsidiaria.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(subsidiaria.getRemovidoEm())
                .isNotNull();
    }

    private EmpresaModel criarEmpresa() {
        return new EmpresaModel(
                new EmpresaRecord("Empresa Exemplo")
        );
    }
}