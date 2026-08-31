package com.empresa.erp.domain.organizacao.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.base.model.StatusEnum;

class OrganizacaoModelTest {

    @Test
    @DisplayName("Deve criar organização ativa e normalizar nome")
    void deveCriarOrganizacaoAtivaENormalizarNome() {
        var organizacao = new OrganizacaoModel(
                "  Organização   Exemplo  "
        );

        assertThat(organizacao.getNome())
                .isEqualTo("Organização Exemplo");

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve manter nome nulo para validação na camada de entrada")
    void deveManterNomeNuloParaValidacaoNaCamadaDeEntrada() {
        var organizacao = new OrganizacaoModel(null);

        assertThat(organizacao.getNome()).isNull();

        assertThat(organizacao.getStatus())
                .isEqualTo(StatusEnum.ATIVO);
    }
}