package com.empresa.erp.domain.configuracao.subsidiaria.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class SubsidiariaResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe a partir do model")
    void deveCriarDetalheAPartirDoModel() {
        var subsidiaria = criarSubsidiaria();

        var detalhe =
                new DetalheSubsidiariaRecord(
                        subsidiaria
                );

        assertThat(detalhe.id())
                .isEqualTo(2L);

        assertThat(detalhe.idEmpresa())
                .isEqualTo(1L);

        assertThat(detalhe.empresa())
                .isEqualTo("Empresa Exemplo");

        assertThat(detalhe.nome())
                .isEqualTo("Filial Curitiba");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar item de listagem a partir do model")
    void deveCriarItemDeListagemAPartirDoModel() {
        var subsidiaria = criarSubsidiaria();

        var lista =
                new ListaSubsidiariaRecord(
                        subsidiaria
                );

        assertThat(lista.id())
                .isEqualTo(2L);

        assertThat(lista.idEmpresa())
                .isEqualTo(1L);

        assertThat(lista.empresa())
                .isEqualTo("Empresa Exemplo");

        assertThat(lista.nome())
                .isEqualTo("Filial Curitiba");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo")
    void deveRefletirStatusInativo() {
        var subsidiaria = criarSubsidiaria();

        subsidiaria.inativar();

        var detalhe =
                new DetalheSubsidiariaRecord(
                        subsidiaria
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private SubsidiariaModel criarSubsidiaria() {
        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                1L
        );

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                1L
        );

        var subsidiaria = new SubsidiariaModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                subsidiaria,
                "id",
                2L
        );

        return subsidiaria;
    }
}