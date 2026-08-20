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
        factory =
                Validation
                        .buildDefaultValidatorFactory();

        validator =
                factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        factory.close();
    }

    @Test
    @DisplayName("Deve validar UsuarioRecord valido")
    void deveValidarUsuarioRecordValido() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "Senha@123"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com email em branco"
    )
    void deveInvalidarUsuarioRecordComEmailEmBranco() {
        var dados = new UsuarioRecord(
                "",
                "Senha@123"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "email"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com email invalido"
    )
    void deveInvalidarUsuarioRecordComEmailInvalido() {
        var dados = new UsuarioRecord(
                "email-invalido",
                "Senha@123"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "email"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com senha em branco"
    )
    void deveInvalidarUsuarioRecordComSenhaEmBranco() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                ""
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "senha"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com senha curta"
    )
    void deveInvalidarUsuarioRecordComSenhaCurta() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "Se@1"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "senha"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com senha sem letra maiuscula"
    )
    void deveInvalidarUsuarioRecordComSenhaSemMaiuscula() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "senha@123"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "senha"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com senha sem letra minuscula"
    )
    void deveInvalidarUsuarioRecordComSenhaSemMinuscula() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "SENHA@123"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "senha"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com senha sem numero"
    )
    void deveInvalidarUsuarioRecordComSenhaSemNumero() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "Senha@abc"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "senha"
                                )
                );
    }

    @Test
    @DisplayName(
            "Deve invalidar UsuarioRecord "
                    + "com senha sem caractere especial"
    )
    void deveInvalidarUsuarioRecordComSenhaSemCaractereEspecial() {
        var dados = new UsuarioRecord(
                "usuario@teste.com",
                "Senha123"
        );

        var violacoes =
                validar(dados);

        assertThat(violacoes)
                .anyMatch(
                        violacao ->
                                ehCampo(
                                        violacao,
                                        "senha"
                                )
                );
    }

    private Set<ConstraintViolation<Object>> validar(
            Object dados
    ) {
        return validator.validate(dados);
    }

    private boolean ehCampo(
            ConstraintViolation<?> violacao,
            String campo
    ) {
        return violacao
                .getPropertyPath()
                .toString()
                .equals(campo);
    }
}