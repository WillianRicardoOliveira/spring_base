package com.empresa.erp.domain.configuracao.estabelecimento.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class EstabelecimentoResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe a partir do model")
    void deveCriarDetalheAPartirDoModel() {
        var estabelecimento = criarEstabelecimento();

        var detalhe =
                new DetalheEstabelecimentoRecord(
                        estabelecimento
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

        assertThat(detalhe.auditoria())
                .isNotNull();
    }

    @Test
    @DisplayName("Deve criar item de listagem a partir do model")
    void deveCriarItemDeListagemAPartirDoModel() {
        var estabelecimento = criarEstabelecimento();

        var lista =
                new ListaEstabelecimentoRecord(
                        estabelecimento
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
        var estabelecimento = criarEstabelecimento();

        estabelecimento.inativar();

        var detalhe =
                new DetalheEstabelecimentoRecord(
                        estabelecimento
                );

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    private EstabelecimentoModel criarEstabelecimento() {
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

        var estabelecimento = new EstabelecimentoModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                estabelecimento,
                "id",
                2L
        );

        return estabelecimento;
    }
}