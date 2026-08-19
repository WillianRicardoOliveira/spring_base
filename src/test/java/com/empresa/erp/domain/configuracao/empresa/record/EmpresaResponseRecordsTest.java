package com.empresa.erp.domain.configuracao.empresa.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class EmpresaResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe da empresa a partir do model")
    void deveCriarDetalheDaEmpresaAPartirDoModel() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        var detalhe =
                new DetalheEmpresaRecord(empresa);

        assertThat(detalhe.id())
                .isEqualTo(1L);

        assertThat(detalhe.nome())
                .isEqualTo("Empresa Exemplo");

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve criar item de listagem a partir do model")
    void deveCriarItemDeListagemAPartirDoModel() {
        var empresa = criarEmpresa(
                2L,
                "Segunda Empresa"
        );

        var lista =
                new ListaEmpresaRecord(empresa);

        assertThat(lista.id())
                .isEqualTo(2L);

        assertThat(lista.nome())
                .isEqualTo("Segunda Empresa");

        assertThat(lista.status())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve refletir status inativo no detalhe")
    void deveRefletirStatusInativoNoDetalhe() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        empresa.inativar();

        var detalhe =
                new DetalheEmpresaRecord(empresa);

        assertThat(detalhe.status())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve refletir status removido na listagem")
    void deveRefletirStatusRemovidoNaListagem() {
        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        empresa.remover(10L);

        var lista =
                new ListaEmpresaRecord(empresa);

        assertThat(lista.status())
                .isEqualTo(StatusEnum.REMOVIDO);
    }

    private EmpresaModel criarEmpresa(
            Long id,
            String nome
    ) {
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
                new EmpresaRecord(nome)
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                id
        );

        return empresa;
    }
}