package com.empresa.erp.domain.acesso.perfil.record;

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

class PerfilRecordsValidationTest {

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
    @DisplayName("Deve validar PerfilRecord valido")
    void deveValidarPerfilRecordValido() {
        var dados = new PerfilRecord("Administrador", "Perfil administrador");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar PerfilRecord com nome em branco")
    void deveInvalidarPerfilRecordComNomeEmBranco() {
        var dados = new PerfilRecord("", "Perfil administrador");

        assertThat(camposComErro(dados)).contains("nome");
    }

    @Test
    @DisplayName("Deve validar PerfilRecord com descricao nula")
    void deveValidarPerfilRecordComDescricaoNula() {
        var dados = new PerfilRecord("Administrador", null);

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve validar AtualizaPerfilRecord valido")
    void deveValidarAtualizaPerfilRecordValido() {
        var dados = new AtualizaPerfilRecord(1L, "Administrador", "Perfil administrador");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar AtualizaPerfilRecord sem id")
    void deveInvalidarAtualizaPerfilRecordSemId() {
        var dados = new AtualizaPerfilRecord(null, "Administrador", "Perfil administrador");

        assertThat(camposComErro(dados)).contains("id");
    }

    @Test
    @DisplayName("Deve invalidar AtualizaPerfilRecord com nome em branco")
    void deveInvalidarAtualizaPerfilRecordComNomeEmBranco() {
        var dados = new AtualizaPerfilRecord(1L, "", "Perfil administrador");

        assertThat(camposComErro(dados)).contains("nome");
    }

    @Test
    @DisplayName("Deve validar AtualizaPerfilRecord com descricao nula")
    void deveValidarAtualizaPerfilRecordComDescricaoNula() {
        var dados = new AtualizaPerfilRecord(1L, "Administrador", null);

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}