package com.empresa.erp.domain.configuracao.empresa.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class EmpresaRecordsValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation
                .buildDefaultValidatorFactory()
                .getValidator();
    }

    @Test
    @DisplayName("Deve aceitar cadastro de empresa válido")
    void deveAceitarCadastroDeEmpresaValido() {
        var dados = new EmpresaRecord("Empresa Exemplo");

        Set<ConstraintViolation<EmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome nulo")
    void deveRejeitarCadastroComNomeNulo() {
        var dados = new EmpresaRecord(null);

        Set<ConstraintViolation<EmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome em branco")
    void deveRejeitarCadastroComNomeEmBranco() {
        var dados = new EmpresaRecord(" ");

        Set<ConstraintViolation<EmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome acima do limite")
    void deveRejeitarCadastroComNomeAcimaDoLimite() {
        var dados = new EmpresaRecord("A".repeat(101));

        Set<ConstraintViolation<EmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve aceitar atualização válida")
    void deveAceitarAtualizacaoValida() {
        var dados = new AtualizaEmpresaRecord(
                1L,
                "Empresa Atualizada"
        );

        Set<ConstraintViolation<AtualizaEmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualização sem id")
    void deveRejeitarAtualizacaoSemId() {
        var dados = new AtualizaEmpresaRecord(
                null,
                "Empresa Atualizada"
        );

        Set<ConstraintViolation<AtualizaEmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualização com nome em branco")
    void deveRejeitarAtualizacaoComNomeEmBranco() {
        var dados = new AtualizaEmpresaRecord(1L, "");

        Set<ConstraintViolation<AtualizaEmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualização com nome acima do limite")
    void deveRejeitarAtualizacaoComNomeAcimaDoLimite() {
        var dados = new AtualizaEmpresaRecord(
                1L,
                "A".repeat(101)
        );

        Set<ConstraintViolation<AtualizaEmpresaRecord>> violacoes =
                validator.validate(dados);

        assertThat(violacoes).isNotEmpty();
    }
}