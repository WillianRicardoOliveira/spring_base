package com.empresa.erp.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class ConfiguracaoAmbienteTest {

    @Test
    @DisplayName(
            "Deve manter configurações isoladas e seguras no ambiente de testes"
    )
    void deveManterConfiguracoesSegurasNoAmbienteDeTestes()
            throws IOException {
        Properties properties =
                carregarProperties(
                        "application-test.properties"
                );

        assertThat(
                properties.getProperty(
                        "spring.datasource.url"
                )
        ).startsWith(
                "jdbc:h2:mem:"
        );

        assertThat(
                properties.getProperty(
                        "spring.flyway.enabled"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "spring.jpa.hibernate.ddl-auto"
                )
        ).isEqualTo(
                "create-drop"
        );

        assertThat(
                properties.getProperty(
                        "app.bootstrap.enabled"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "app.security.swagger-public"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "springdoc.api-docs.enabled"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "springdoc.swagger-ui.enabled"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "spring.mail.properties.mail.smtp.auth"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "spring.mail.properties.mail.smtp.starttls.enable"
                )
        ).isEqualTo(
                "false"
        );
    }

    @Test
    @DisplayName(
            "Deve manter configurações seguras no ambiente de produção"
    )
    void deveManterConfiguracoesSegurasNoAmbienteDeProducao()
            throws IOException {
        Properties properties =
                carregarProperties(
                        "application-prod.properties"
                );

        assertThat(
                properties.getProperty(
                        "spring.flyway.enabled"
                )
        ).isEqualTo(
                "true"
        );

        assertThat(
                properties.getProperty(
                        "spring.jpa.hibernate.ddl-auto"
                )
        ).isEqualTo(
                "validate"
        );

        assertThat(
                properties.getProperty(
                        "app.bootstrap.enabled"
                )
        ).isEqualTo(
                "${BOOTSTRAP_ENABLED:false}"
        );

        assertThat(
                properties.getProperty(
                        "app.security.swagger-public"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "springdoc.api-docs.enabled"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "springdoc.swagger-ui.enabled"
                )
        ).isEqualTo(
                "false"
        );

        assertThat(
                properties.getProperty(
                        "app.cors.allowed-origins"
                )
        ).isEqualTo(
                "${CORS_ALLOWED_ORIGINS}"
        );

        assertThat(
                properties.getProperty(
                        "spring.security.oauth2.resourceserver.jwt.issuer-uri"
                )
        ).isEqualTo(
                "${AZURE_ISSUER_URI}"
        );

        assertThat(
                properties.getProperty(
                        "app.convite-organizacao.url-aceite"
                )
        ).isEqualTo(
                "${CONVITE_ORGANIZACAO_URL_ACEITE}"
        );

        assertThat(
                properties.getProperty(
                        "spring.mail.host"
                )
        ).isEqualTo(
                "${MAIL_HOST}"
        );

        assertThat(
                properties.getProperty(
                        "spring.mail.password"
                )
        ).isEqualTo(
                "${MAIL_PASSWORD}"
        );
    }

    @Test
    @DisplayName(
            "Não deve possuir credenciais literais sensíveis na produção"
    )
    void naoDevePossuirCredenciaisLiteraisSensiveisNaProducao()
            throws IOException {
        Properties properties =
                carregarProperties(
                        "application-prod.properties"
                );

        assertThat(
                properties.getProperty(
                        "spring.mail.username"
                )
        ).isEqualTo(
                "${MAIL_USERNAME}"
        );

        assertThat(
                properties.getProperty(
                        "spring.mail.password"
                )
        ).isEqualTo(
                "${MAIL_PASSWORD}"
        );

        assertThat(
                properties.getProperty(
                        "sso.audience"
                )
        ).isEqualTo(
                "${AZURE_API_AUDIENCE}"
        );

        assertThat(
                properties.getProperty(
                        "sso.scope"
                )
        ).isEqualTo(
                "${AZURE_API_SCOPE}"
        );
    }

    private Properties carregarProperties(
            String nomeArquivo
    ) throws IOException {
        ClassPathResource resource =
                new ClassPathResource(nomeArquivo);

        assertThat(resource.exists())
                .isTrue();

        Properties properties =
                new Properties();

        try (InputStream inputStream =
                     resource.getInputStream()) {
            properties.load(inputStream);
        }

        return properties;
    }
}