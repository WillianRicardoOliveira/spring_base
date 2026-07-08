package com.empresa.erp.domain.usuario.record;

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

class UsuarioRecordsValidationTest {

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
    @DisplayName("Deve validar UsuarioRecord valido")
    void deveValidarUsuarioRecordValido() {
        var dados = new UsuarioRecord("usuario@teste.com", "123456");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com email em branco")
    void deveInvalidarUsuarioRecordComEmailEmBranco() {
        var dados = new UsuarioRecord("", "123456");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com email invalido")
    void deveInvalidarUsuarioRecordComEmailInvalido() {
        var dados = new UsuarioRecord("email-invalido", "123456");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha em branco")
    void deveInvalidarUsuarioRecordComSenhaEmBranco() {
        var dados = new UsuarioRecord("usuario@teste.com", "");

        assertThat(camposComErro(dados)).contains("senha");
    }

    @Test
    @DisplayName("Deve validar AtualizaUsuarioRecord valido")
    void deveValidarAtualizaUsuarioRecordValido() {
        var dados = new AtualizaUsuarioRecord(1L, "usuario@teste.com");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar AtualizaUsuarioRecord sem id")
    void deveInvalidarAtualizaUsuarioRecordSemId() {
        var dados = new AtualizaUsuarioRecord(null, "usuario@teste.com");

        assertThat(camposComErro(dados)).contains("id");
    }

    @Test
    @DisplayName("Deve invalidar AtualizaUsuarioRecord com email em branco")
    void deveInvalidarAtualizaUsuarioRecordComEmailEmBranco() {
        var dados = new AtualizaUsuarioRecord(1L, "");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve invalidar AtualizaUsuarioRecord com email invalido")
    void deveInvalidarAtualizaUsuarioRecordComEmailInvalido() {
        var dados = new AtualizaUsuarioRecord(1L, "email-invalido");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve validar AtualizaSenhaUsuarioRecord valido")
    void deveValidarAtualizaSenhaUsuarioRecordValido() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "nova-senha");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar AtualizaSenhaUsuarioRecord sem id")
    void deveInvalidarAtualizaSenhaUsuarioRecordSemId() {
        var dados = new AtualizaSenhaUsuarioRecord(null, "nova-senha");

        assertThat(camposComErro(dados)).contains("id");
    }

    @Test
    @DisplayName("Deve invalidar AtualizaSenhaUsuarioRecord com senha em branco")
    void deveInvalidarAtualizaSenhaUsuarioRecordComSenhaEmBranco() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "");

        assertThat(camposComErro(dados)).contains("senha");
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}