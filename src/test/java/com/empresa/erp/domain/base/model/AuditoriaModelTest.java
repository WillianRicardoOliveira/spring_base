package com.empresa.erp.domain.base.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuditoriaModelTest {

    @Test
    @DisplayName("Deve registrar dados de remocao")
    void deveRegistrarDadosDeRemocao() {
        var entidade = new EntidadeAuditavelTeste();

        entidade.remover(10L);

        assertThat(entidade.getRemovidoPor()).isEqualTo(10L);
        assertThat(entidade.getRemovidoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve iniciar sem dados de remocao")
    void deveIniciarSemDadosDeRemocao() {
        var entidade = new EntidadeAuditavelTeste();

        assertThat(entidade.getRemovidoPor()).isNull();
        assertThat(entidade.getRemovidoEm()).isNull();
    }

    private static class EntidadeAuditavelTeste extends AuditoriaModel {

        void remover(Long idUsuario) {
            registrarRemocao(idUsuario);
        }
    }
}