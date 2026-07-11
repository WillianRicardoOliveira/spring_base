package com.empresa.erp.core.security.record;

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

class LoginSecurityValidationTest {

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
    @DisplayName("Deve validar LoginSecurity com email e senha informados")
    void deveValidarLoginSecurityComEmailESenhaInformados() {
        var dados = new LoginSecurity("usuario@teste.com", "123456");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar LoginSecurity com email em branco")
    void deveInvalidarLoginSecurityComEmailEmBranco() {
        var dados = new LoginSecurity("", "123456");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve invalidar LoginSecurity com email nulo")
    void deveInvalidarLoginSecurityComEmailNulo() {
        var dados = new LoginSecurity(null, "123456");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve invalidar LoginSecurity com email invalido")
    void deveInvalidarLoginSecurityComEmailInvalido() {
        var dados = new LoginSecurity("email-invalido", "123456");

        assertThat(camposComErro(dados)).contains("email");
    }

    @Test
    @DisplayName("Deve invalidar LoginSecurity com senha em branco")
    void deveInvalidarLoginSecurityComSenhaEmBranco() {
        var dados = new LoginSecurity("usuario@teste.com", "");

        assertThat(camposComErro(dados)).contains("senha");
    }

    @Test
    @DisplayName("Deve invalidar LoginSecurity com senha nula")
    void deveInvalidarLoginSecurityComSenhaNula() {
        var dados = new LoginSecurity("usuario@teste.com", null);

        assertThat(camposComErro(dados)).contains("senha");
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
