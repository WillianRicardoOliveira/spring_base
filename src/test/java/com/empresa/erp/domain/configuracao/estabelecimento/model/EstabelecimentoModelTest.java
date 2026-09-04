package com.empresa.erp.domain.configuracao.estabelecimento.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.AtualizaEstabelecimentoRecord;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

class EstabelecimentoModelTest {

    @Test
    @DisplayName("Deve criar estabelecimento ativo")
    void deveCriarEstabelecimentoAtivo() {
        var empresa = criarEmpresa();

        var estabelecimento = new EstabelecimentoModel(
                empresa,
                "Filial Curitiba"
        );

        assertThat(estabelecimento.getEmpresa())
                .isSameAs(empresa);

        assertThat(estabelecimento.getNome())
                .isEqualTo("Filial Curitiba");

        assertThat(estabelecimento.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve normalizar nome ao criar estabelecimento")
    void deveNormalizarNomeAoCriarEstabelecimento() {
        var estabelecimento = new EstabelecimentoModel(
                criarEmpresa(),
                "  Filial   Curitiba  "
        );

        assertThat(estabelecimento.getNome())
                .isEqualTo("Filial Curitiba");
    }

    @Test
    @DisplayName("Deve atualizar e normalizar nome")
    void deveAtualizarENormalizarNome() {
        var estabelecimento = new EstabelecimentoModel(
                criarEmpresa(),
                "Filial Curitiba"
        );

        estabelecimento.atualizar(
                new AtualizaEstabelecimentoRecord(
                        1L,
                        "  Filial   Parana  "
                )
        );

        assertThat(estabelecimento.getNome())
                .isEqualTo("Filial Parana");
    }

    @Test
    @DisplayName("Deve inativar estabelecimento")
    void deveInativarEstabelecimento() {
        var estabelecimento = new EstabelecimentoModel(
                criarEmpresa(),
                "Filial Curitiba"
        );

        estabelecimento.inativar();

        assertThat(estabelecimento.getStatus())
                .isEqualTo(StatusEnum.INATIVO);
    }

    @Test
    @DisplayName("Deve remover estabelecimento com auditoria")
    void deveRemoverEstabelecimentoComAuditoria() {
        var estabelecimento = new EstabelecimentoModel(
                criarEmpresa(),
                "Filial Curitiba"
        );

        estabelecimento.remover(10L);

        assertThat(estabelecimento.getStatus())
                .isEqualTo(StatusEnum.REMOVIDO);

        assertThat(estabelecimento.getRemovidoPor())
                .isEqualTo(10L);

        assertThat(estabelecimento.getRemovidoEm())
                .isNotNull();
    }

    private EmpresaModel criarEmpresa() {
        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        return new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );
    }
}