package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class UsuarioEmpresaRecordsValidationTest {

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
        var dados = new UsuarioEmpresaRecord(
                1L,
                2L,
                true
        );

        assertThat(validator.validate(dados))
                .isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro sem usuario")
    void deveRejeitarCadastroSemUsuario() {
        var dados = new UsuarioEmpresaRecord(
                null,
                2L,
                true
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar cadastro sem empresa")
    void deveRejeitarCadastroSemEmpresa() {
        var dados = new UsuarioEmpresaRecord(
                1L,
                null,
                true
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName(
            "Deve rejeitar cadastro sem definicao de estabelecimentos"
    )
    void deveRejeitarCadastroSemDefinicaoDeEstabelecimentos() {
        var dados = new UsuarioEmpresaRecord(
                1L,
                2L,
                null
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName("Deve aceitar atualizacao valida")
    void deveAceitarAtualizacaoValida() {
        var dados = new AtualizaUsuarioEmpresaRecord(
                1L,
                false
        );

        assertThat(validator.validate(dados))
                .isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar atualizacao sem id")
    void deveRejeitarAtualizacaoSemId() {
        var dados = new AtualizaUsuarioEmpresaRecord(
                null,
                false
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }

    @Test
    @DisplayName(
            "Deve rejeitar atualizacao sem definicao de estabelecimentos"
    )
    void deveRejeitarAtualizacaoSemDefinicaoDeEstabelecimentos() {
        var dados = new AtualizaUsuarioEmpresaRecord(
                1L,
                null
        );

        assertThat(validator.validate(dados))
                .isNotEmpty();
    }
}