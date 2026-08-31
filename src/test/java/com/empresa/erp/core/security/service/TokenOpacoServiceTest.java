package com.empresa.erp.core.security.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenOpacoServiceTest {

    private TokenOpacoService service;

    @BeforeEach
    void setUp() {
        service = new TokenOpacoService();
    }

    @Test
    @DisplayName(
            "Deve gerar token no formato Base64 URL seguro"
    )
    void deveGerarTokenNoFormatoBase64UrlSeguro() {
        String token =
                service.gerar();

        assertThat(token)
                .isNotBlank()
                .hasSize(43)
                .matches(
                        "[A-Za-z0-9_-]+"
                );
    }

    @Test
    @DisplayName(
            "Deve gerar tokens diferentes"
    )
    void deveGerarTokensDiferentes() {
        String primeiroToken =
                service.gerar();

        String segundoToken =
                service.gerar();

        assertThat(primeiroToken)
                .isNotEqualTo(
                        segundoToken
                );
    }

    @Test
    @DisplayName(
            "Deve gerar hash SHA-256 no formato Base64 URL seguro"
    )
    void deveGerarHashSha256NoFormatoBase64UrlSeguro() {
        String hash =
                service.gerarHash(
                        "token-teste"
                );

        assertThat(hash)
                .isEqualTo(
                        "hXxPSs7Dj1S_PhQuZ1afD8dTMTjUN31P"
                                + "It2YtZvvVXM"
                )
                .hasSize(43)
                .matches(
                        "[A-Za-z0-9_-]+"
                );
    }

    @Test
    @DisplayName(
            "Deve gerar o mesmo hash para o mesmo token"
    )
    void deveGerarOMesmoHashParaOMesmoToken() {
        String primeiroHash =
                service.gerarHash(
                        "token-teste"
                );

        String segundoHash =
                service.gerarHash(
                        "token-teste"
                );

        assertThat(primeiroHash)
                .isEqualTo(
                        segundoHash
                );
    }

    @Test
    @DisplayName(
            "Deve gerar hashes diferentes para tokens diferentes"
    )
    void deveGerarHashesDiferentesParaTokensDiferentes() {
        String primeiroHash =
                service.gerarHash(
                        "primeiro-token"
                );

        String segundoHash =
                service.gerarHash(
                        "segundo-token"
                );

        assertThat(primeiroHash)
                .isNotEqualTo(
                        segundoHash
                );
    }

    @Test
    @DisplayName(
            "Não deve gerar hash para token nulo"
    )
    void naoDeveGerarHashParaTokenNulo() {
        assertThatThrownBy(
                () -> service.gerarHash(null)
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Token obrigatorio."
                );
    }

    @Test
    @DisplayName(
            "Não deve gerar hash para token em branco"
    )
    void naoDeveGerarHashParaTokenEmBranco() {
        assertThatThrownBy(
                () -> service.gerarHash("   ")
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Token obrigatorio."
                );
    }
}