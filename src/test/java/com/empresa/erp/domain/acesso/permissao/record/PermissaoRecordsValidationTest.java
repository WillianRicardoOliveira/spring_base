package com.empresa.erp.domain.acesso.permissao.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class PermissaoRecordsValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Deve validar PermissaoRecord valido")
    void deveValidarPermissaoRecordValido() {
        var dados = new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar PermissaoRecord com nome em branco")
    void deveInvalidarPermissaoRecordComNomeEmBranco() {
        var dados = new PermissaoRecord(
                "",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        assertThat(camposComErro(dados)).contains("nome");
    }

    @Test
    @DisplayName("Deve invalidar PermissaoRecord com chave em branco")
    void deveInvalidarPermissaoRecordComChaveEmBranco() {
        var dados = new PermissaoRecord(
                "Listar usuarios",
                "",
                "Permite listar usuarios"
        );

        assertThat(camposComErro(dados)).contains("chave");
    }

    @Test
    @DisplayName("Deve validar PermissaoRecord com descricao nula")
    void deveValidarPermissaoRecordComDescricaoNula() {
        var dados = new PermissaoRecord(
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                null
        );

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve validar AtualizaPermissaoRecord valido")
    void deveValidarAtualizaPermissaoRecordValido() {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar AtualizaPermissaoRecord sem id")
    void deveInvalidarAtualizaPermissaoRecordSemId() {
        var dados = new AtualizaPermissaoRecord(
                null,
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        assertThat(camposComErro(dados)).contains("id");
    }

    @Test
    @DisplayName("Deve invalidar AtualizaPermissaoRecord com nome em branco")
    void deveInvalidarAtualizaPermissaoRecordComNomeEmBranco() {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "",
                "ACESSO_USUARIO_LISTAR",
                "Permite listar usuarios"
        );

        assertThat(camposComErro(dados)).contains("nome");
    }

    @Test
    @DisplayName("Deve invalidar AtualizaPermissaoRecord com chave em branco")
    void deveInvalidarAtualizaPermissaoRecordComChaveEmBranco() {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar usuarios",
                "",
                "Permite listar usuarios"
        );

        assertThat(camposComErro(dados)).contains("chave");
    }

    @Test
    @DisplayName("Deve validar AtualizaPermissaoRecord com descricao nula")
    void deveValidarAtualizaPermissaoRecordComDescricaoNula() {
        var dados = new AtualizaPermissaoRecord(
                1L,
                "Listar usuarios",
                "ACESSO_USUARIO_LISTAR",
                null
        );

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}