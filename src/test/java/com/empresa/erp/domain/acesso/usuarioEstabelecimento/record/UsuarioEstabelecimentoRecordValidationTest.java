package com.empresa.erp.domain.acesso.usuarioEstabelecimento.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class UsuarioEstabelecimentoRecordValidationTest {

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
        var dados = new UsuarioEstabelecimentoRecord(
                1L,
                2L
        );

        assertThat(validator.validate(dados))
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve rejeitar cadastro sem usuario empresa"
    )
    void deveRejeitarCadastroSemUsuarioEmpresa() {
        var dados = new UsuarioEstabelecimentoRecord(
                null,
                2L
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName(
            "Deve rejeitar cadastro sem estabelecimento"
    )
    void deveRejeitarCadastroSemEstabelecimento() {
        var dados = new UsuarioEstabelecimentoRecord(
                1L,
                null
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }
}