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

class SsoLoginSecurityValidationTest {

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
    @DisplayName("Deve validar SsoLoginSecurity com token informado")
    void deveValidarSsoLoginSecurityComTokenInformado() {
        var dados = new SsoLoginSecurity("sso-token");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar SsoLoginSecurity com token em branco")
    void deveInvalidarSsoLoginSecurityComTokenEmBranco() {
        var dados = new SsoLoginSecurity("");

        assertThat(camposComErro(dados)).contains("token");
    }

    @Test
    @DisplayName("Deve invalidar SsoLoginSecurity com token nulo")
    void deveInvalidarSsoLoginSecurityComTokenNulo() {
        var dados = new SsoLoginSecurity(null);

        assertThat(camposComErro(dados)).contains("token");
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}