package com.empresa.erp.domain.configuracao.subsidiaria.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class SubsidiariaRecordsValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    @DisplayName("Deve aceitar cadastro valido")
    void deveAceitarCadastroValido() {
        var dados = new SubsidiariaRecord(
                1L,
                "Filial Curitiba"
        );

        assertThat(validator.validate(dados)).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro sem empresa")
    void deveRejeitarCadastroSemEmpresa() {
        var dados = new SubsidiariaRecord(
                null,
                "Filial Curitiba"
        );

        Set<ConstraintViolation<SubsidiariaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome nulo")
    void deveRejeitarCadastroComNomeNulo() {
        var dados = new SubsidiariaRecord(1L, null);

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome em branco")
    void deveRejeitarCadastroComNomeEmBranco() {
        var dados = new SubsidiariaRecord(1L, " ");

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome acima do limite")
    void deveRejeitarCadastroComNomeAcimaDoLimite() {
        var dados = new SubsidiariaRecord(
                1L,
                "A".repeat(101)
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve aceitar atualizacao valida")
    void deveAceitarAtualizacaoValida() {
        var dados = new AtualizaSubsidiariaRecord(
                1L,
                "Filial Atualizada"
        );

        assertThat(validator.validate(dados)).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualizacao sem id")
    void deveRejeitarAtualizacaoSemId() {
        var dados = new AtualizaSubsidiariaRecord(
                null,
                "Filial Atualizada"
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualizacao com nome em branco")
    void deveRejeitarAtualizacaoComNomeEmBranco() {
        var dados = new AtualizaSubsidiariaRecord(
                1L,
                ""
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualizacao com nome acima do limite")
    void deveRejeitarAtualizacaoComNomeAcimaDoLimite() {
        var dados = new AtualizaSubsidiariaRecord(
                1L,
                "A".repeat(101)
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }
}