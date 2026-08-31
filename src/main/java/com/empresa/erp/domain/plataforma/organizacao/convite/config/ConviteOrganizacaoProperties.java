package com.empresa.erp.domain.plataforma.organizacao.convite.config;

import java.net.URI;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "app.convite-organizacao"
)
public record ConviteOrganizacaoProperties(
        Duration validade,
        URI urlAceite,
        String remetente
) {

    public ConviteOrganizacaoProperties {
        if (validade == null
                || validade.isZero()
                || validade.isNegative()) {
            throw new IllegalArgumentException(
                    "A validade do convite da organizacao "
                            + "deve ser maior que zero."
            );
        }

        if (urlAceite == null
                || !urlAceite.isAbsolute()
                || !possuiEsquemaHttp(urlAceite)) {
            throw new IllegalArgumentException(
                    "A URL de aceite do convite da organizacao "
                            + "deve ser uma URL HTTP ou HTTPS absoluta."
            );
        }

        if (remetente == null || remetente.isBlank()) {
            throw new IllegalArgumentException(
                    "O remetente do convite da organizacao "
                            + "e obrigatorio."
            );
        }

        remetente = remetente.trim();
    }

    private static boolean possuiEsquemaHttp(
            URI url
    ) {
        return "http".equalsIgnoreCase(url.getScheme())
                || "https".equalsIgnoreCase(
                        url.getScheme()
                );
    }
}