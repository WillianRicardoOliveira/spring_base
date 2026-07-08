package com.empresa.erp.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreExceptionsTest {

    @Test
    @DisplayName("Deve criar ValidacaoException com mensagem")
    void deveCriarValidacaoExceptionComMensagem() {
        var exception = new ValidacaoException("Regra de negocio invalida");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Regra de negocio invalida");

        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Deve criar SsoAuthenticationException com mensagem")
    void deveCriarSsoAuthenticationExceptionComMensagem() {
        var exception = new SsoAuthenticationException("Token SSO invalido");

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token SSO invalido");

        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Deve criar SsoAuthenticationException com causa")
    void deveCriarSsoAuthenticationExceptionComCausa() {
        var cause = new RuntimeException("jwt invalido");

        var exception = new SsoAuthenticationException("Token SSO invalido ou expirado", cause);

        assertThat(exception)
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token SSO invalido ou expirado")
                .hasCause(cause);
    }
}