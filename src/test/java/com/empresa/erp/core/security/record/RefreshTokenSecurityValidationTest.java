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

class RefreshTokenSecurityValidationTest {

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
    @DisplayName("Deve validar RefreshTokenSecurity com refresh token informado")
    void deveValidarRefreshTokenSecurityComRefreshTokenInformado() {
        var dados = new RefreshTokenSecurity("refresh-token");

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar RefreshTokenSecurity com refresh token em branco")
    void deveInvalidarRefreshTokenSecurityComRefreshTokenEmBranco() {
        var dados = new RefreshTokenSecurity("");

        assertThat(camposComErro(dados)).contains("refreshToken");
    }

    @Test
    @DisplayName("Deve invalidar RefreshTokenSecurity com refresh token nulo")
    void deveInvalidarRefreshTokenSecurityComRefreshTokenNulo() {
        var dados = new RefreshTokenSecurity(null);

        assertThat(camposComErro(dados)).contains("refreshToken");
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}