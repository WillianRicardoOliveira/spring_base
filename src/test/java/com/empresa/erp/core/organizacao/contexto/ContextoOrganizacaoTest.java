package com.empresa.erp.core.organizacao.contexto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.core.exception.ValidacaoException;

class ContextoOrganizacaoTest {

    @Test
    @DisplayName("Deve definir e retornar organização")
    void deveDefinirERetornarOrganizacao() {
        var contexto = new ContextoOrganizacao();

        contexto.definir(10L);

        assertThat(contexto.getIdOrganizacao())
                .isEqualTo(10L);
    }

    @Test
    @DisplayName("Deve permitir definir novamente a mesma organização")
    void devePermitirDefinirNovamenteMesmaOrganizacao() {
        var contexto = new ContextoOrganizacao();

        contexto.definir(10L);
        contexto.definir(10L);

        assertThat(contexto.getIdOrganizacao())
                .isEqualTo(10L);
    }

    @Test
    @DisplayName("Não deve retornar organização não informada")
    void naoDeveRetornarOrganizacaoNaoInformada() {
        var contexto = new ContextoOrganizacao();

        assertThatThrownBy(contexto::getIdOrganizacao)
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao nao informada.");
    }

    @Test
    @DisplayName("Não deve aceitar organização nula")
    void naoDeveAceitarOrganizacaoNula() {
        var contexto = new ContextoOrganizacao();

        assertThatThrownBy(() -> contexto.definir(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao invalida.");
    }

    @Test
    @DisplayName("Não deve aceitar organização igual a zero")
    void naoDeveAceitarOrganizacaoIgualAZero() {
        var contexto = new ContextoOrganizacao();

        assertThatThrownBy(() -> contexto.definir(0L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao invalida.");
    }

    @Test
    @DisplayName("Não deve aceitar organização negativa")
    void naoDeveAceitarOrganizacaoNegativa() {
        var contexto = new ContextoOrganizacao();

        assertThatThrownBy(() -> contexto.definir(-1L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Organizacao invalida.");
    }

    @Test
    @DisplayName("Não deve permitir trocar organização da requisição")
    void naoDevePermitirTrocarOrganizacaoDaRequisicao() {
        var contexto = new ContextoOrganizacao();

        contexto.definir(10L);

        assertThatThrownBy(() -> contexto.definir(20L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Organizacao da requisicao ja definida."
                );

        assertThat(contexto.getIdOrganizacao())
                .isEqualTo(10L);
    }
}