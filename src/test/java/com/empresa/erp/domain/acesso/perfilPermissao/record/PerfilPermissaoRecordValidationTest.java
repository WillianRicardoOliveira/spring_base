package com.empresa.erp.domain.acesso.perfilPermissao.record;

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

class PerfilPermissaoRecordValidationTest {

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
    @DisplayName("Deve validar PerfilPermissaoRecord valido")
    void deveValidarPerfilPermissaoRecordValido() {
        var dados = new PerfilPermissaoRecord(1L, 2L);

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar PerfilPermissaoRecord sem perfil")
    void deveInvalidarPerfilPermissaoRecordSemPerfil() {
        var dados = new PerfilPermissaoRecord(null, 2L);

        assertThat(camposComErro(dados)).contains("idPerfil");
    }

    @Test
    @DisplayName("Deve invalidar PerfilPermissaoRecord sem permissao")
    void deveInvalidarPerfilPermissaoRecordSemPermissao() {
        var dados = new PerfilPermissaoRecord(1L, null);

        assertThat(camposComErro(dados)).contains("idPermissao");
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}