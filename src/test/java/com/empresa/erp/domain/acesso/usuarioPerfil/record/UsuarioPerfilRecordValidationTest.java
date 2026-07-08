package com.empresa.erp.domain.acesso.usuarioPerfil.record;

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

class UsuarioPerfilRecordValidationTest {

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
    @DisplayName("Deve validar UsuarioPerfilRecord valido")
    void deveValidarUsuarioPerfilRecordValido() {
        var dados = new UsuarioPerfilRecord(1L, 2L);

        var violacoes = validator.validate(dados);

        assertThat(violacoes).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar UsuarioPerfilRecord sem usuario")
    void deveInvalidarUsuarioPerfilRecordSemUsuario() {
        var dados = new UsuarioPerfilRecord(null, 2L);

        assertThat(camposComErro(dados)).contains("idUsuario");
    }

    @Test
    @DisplayName("Deve invalidar UsuarioPerfilRecord sem perfil")
    void deveInvalidarUsuarioPerfilRecordSemPerfil() {
        var dados = new UsuarioPerfilRecord(1L, null);

        assertThat(camposComErro(dados)).contains("idPerfil");
    }

    private Set<String> camposComErro(Object dados) {
        return validator.validate(dados).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}