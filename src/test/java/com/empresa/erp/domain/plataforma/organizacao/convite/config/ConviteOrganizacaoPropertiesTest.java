package com.empresa.erp.domain.plataforma.organizacao.convite.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConviteOrganizacaoPropertiesTest {

    private static final Duration VALIDADE =
            Duration.ofHours(48);

    private static final URI URL_ACEITE =
            URI.create(
                    "https://erp.teste.com/convites/aceite"
            );

    private static final String REMETENTE =
            "nao-responda@erp.teste.com";

    @Test
    @DisplayName(
            "Deve criar propriedades válidas"
    )
    void deveCriarPropriedadesValidas() {
        var properties =
                new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URL_ACEITE,
                        REMETENTE
                );

        assertThat(properties.validade())
                .isEqualTo(VALIDADE);

        assertThat(properties.urlAceite())
                .isEqualTo(URL_ACEITE);

        assertThat(properties.remetente())
                .isEqualTo(REMETENTE);
    }

    @Test
    @DisplayName(
            "Deve normalizar remetente"
    )
    void deveNormalizarRemetente() {
        var properties =
                new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URL_ACEITE,
                        "  nao-responda@erp.teste.com  "
                );

        assertThat(properties.remetente())
                .isEqualTo(REMETENTE);
    }

    @Test
    @DisplayName(
            "Não deve aceitar validade nula"
    )
    void naoDeveAceitarValidadeNula() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        null,
                        URL_ACEITE,
                        REMETENTE
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "A validade do convite da organizacao "
                                + "deve ser maior que zero."
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar validade igual a zero"
    )
    void naoDeveAceitarValidadeIgualAZero() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        Duration.ZERO,
                        URL_ACEITE,
                        REMETENTE
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "A validade do convite da organizacao "
                                + "deve ser maior que zero."
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar validade negativa"
    )
    void naoDeveAceitarValidadeNegativa() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        Duration.ofSeconds(-1),
                        URL_ACEITE,
                        REMETENTE
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "A validade do convite da organizacao "
                                + "deve ser maior que zero."
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar URL nula"
    )
    void naoDeveAceitarUrlNula() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        VALIDADE,
                        null,
                        REMETENTE
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "A URL de aceite do convite da organizacao "
                                + "deve ser uma URL HTTP ou HTTPS absoluta."
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar URL relativa"
    )
    void naoDeveAceitarUrlRelativa() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URI.create(
                                "/convites/aceite"
                        ),
                        REMETENTE
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "A URL de aceite do convite da organizacao "
                                + "deve ser uma URL HTTP ou HTTPS absoluta."
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar URL com protocolo não permitido"
    )
    void naoDeveAceitarUrlComProtocoloNaoPermitido() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URI.create(
                                "ftp://erp.teste.com/convites/aceite"
                        ),
                        REMETENTE
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "A URL de aceite do convite da organizacao "
                                + "deve ser uma URL HTTP ou HTTPS absoluta."
                );
    }

    @Test
    @DisplayName(
            "Deve aceitar URL HTTP absoluta"
    )
    void deveAceitarUrlHttpAbsoluta() {
        var url =
                URI.create(
                        "http://localhost:4200/convites/aceite"
                );

        var properties =
                new ConviteOrganizacaoProperties(
                        VALIDADE,
                        url,
                        REMETENTE
                );

        assertThat(properties.urlAceite())
                .isEqualTo(url);
    }

    @Test
    @DisplayName(
            "Não deve aceitar remetente nulo"
    )
    void naoDeveAceitarRemetenteNulo() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URL_ACEITE,
                        null
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "O remetente do convite da organizacao "
                                + "e obrigatorio."
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar remetente em branco"
    )
    void naoDeveAceitarRemetenteEmBranco() {
        assertThatThrownBy(
                () -> new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URL_ACEITE,
                        "   "
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "O remetente do convite da organizacao "
                                + "e obrigatorio."
                );
    }
}