package com.empresa.erp.domain.usuario.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class UsuarioRecordsValidationTest {

    private ValidatorFactory factory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        factory.close();
    }

    @Test
    @DisplayName("Deve validar UsuarioRecord valido")
    void deveValidarUsuarioRecordValido() {
        var dados = new UsuarioRecord("usuario@teste.com", "Senha@123");

        var violacoes = validar(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com email em branco")
    void deveInvalidarUsuarioRecordComEmailEmBranco() {
        var dados = new UsuarioRecord("", "Senha@123");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "email"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com email invalido")
    void deveInvalidarUsuarioRecordComEmailInvalido() {
        var dados = new UsuarioRecord("email-invalido", "Senha@123");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "email"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha em branco")
    void deveInvalidarUsuarioRecordComSenhaEmBranco() {
        var dados = new UsuarioRecord("usuario@teste.com", "");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha curta")
    void deveInvalidarUsuarioRecordComSenhaCurta() {
        var dados = new UsuarioRecord("usuario@teste.com", "Se@1");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha sem letra maiuscula")
    void deveInvalidarUsuarioRecordComSenhaSemMaiuscula() {
        var dados = new UsuarioRecord("usuario@teste.com", "senha@123");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha sem letra minuscula")
    void deveInvalidarUsuarioRecordComSenhaSemMinuscula() {
        var dados = new UsuarioRecord("usuario@teste.com", "SENHA@123");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha sem numero")
    void deveInvalidarUsuarioRecordComSenhaSemNumero() {
        var dados = new UsuarioRecord("usuario@teste.com", "Senha@abc");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve invalidar UsuarioRecord com senha sem caractere especial")
    void deveInvalidarUsuarioRecordComSenhaSemCaractereEspecial() {
        var dados = new UsuarioRecord("usuario@teste.com", "Senha123");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve validar AtualizaUsuarioRecord valido")
    void deveValidarAtualizaUsuarioRecordValido() {
        var dados = new AtualizaUsuarioRecord(1L, "usuario@teste.com");

        var violacoes = validar(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar AtualizaUsuarioRecord sem id")
    void deveInvalidarAtualizaUsuarioRecordSemId() {
        var dados = new AtualizaUsuarioRecord(null, "usuario@teste.com");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "id"));
    }

    @Test
    @DisplayName("Deve invalidar AtualizaUsuarioRecord com email em branco")
    void deveInvalidarAtualizaUsuarioRecordComEmailEmBranco() {
        var dados = new AtualizaUsuarioRecord(1L, "");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "email"));
    }

    @Test
    @DisplayName("Deve invalidar AtualizaUsuarioRecord com email invalido")
    void deveInvalidarAtualizaUsuarioRecordComEmailInvalido() {
        var dados = new AtualizaUsuarioRecord(1L, "email-invalido");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "email"));
    }

    @Test
    @DisplayName("Deve validar AtualizaSenhaUsuarioRecord valido")
    void deveValidarAtualizaSenhaUsuarioRecordValido() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "Senha@123");

        var violacoes = validar(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar AtualizaSenhaUsuarioRecord sem id")
    void deveInvalidarAtualizaSenhaUsuarioRecordSemId() {
        var dados = new AtualizaSenhaUsuarioRecord(null, "Senha@123");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "id"));
    }

    @Test
    @DisplayName("Deve invalidar AtualizaSenhaUsuarioRecord com senha em branco")
    void deveInvalidarAtualizaSenhaUsuarioRecordComSenhaEmBranco() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    @Test
    @DisplayName("Deve invalidar AtualizaSenhaUsuarioRecord com senha fraca")
    void deveInvalidarAtualizaSenhaUsuarioRecordComSenhaFraca() {
        var dados = new AtualizaSenhaUsuarioRecord(1L, "12345678");

        var violacoes = validar(dados);

        assertThat(violacoes).anyMatch(v -> ehCampo(v, "senha"));
    }

    private Set<ConstraintViolation<Object>> validar(Object dados) {
        return validator.validate(dados);
    }

    private boolean ehCampo(ConstraintViolation<?> violacao, String campo) {
        return violacao.getPropertyPath().toString().equals(campo);
    }
}