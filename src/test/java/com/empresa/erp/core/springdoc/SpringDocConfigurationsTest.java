package com.empresa.erp.core.springdoc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.security.SecurityScheme;

class SpringDocConfigurationsTest {

    @Test
    @DisplayName("Deve configurar OpenAPI com esquema de seguranca JWT")
    void deveConfigurarOpenApiComEsquemaDeSegurancaJwt() {
        var config = new SpringDocConfigurations();

        var openAPI = config.customOpenAPI();

        var securityScheme = openAPI
                .getComponents()
                .getSecuritySchemes()
                .get("bearer-key");

        assertThat(securityScheme).isNotNull();
        assertThat(securityScheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(securityScheme.getScheme()).isEqualTo("bearer");
        assertThat(securityScheme.getBearerFormat()).isEqualTo("JWT");
    }

    @Test
    @DisplayName("Deve configurar informacoes da API")
    void deveConfigurarInformacoesDaApi() {
        var config = new SpringDocConfigurations();

        var openAPI = config.customOpenAPI();
        var info = openAPI.getInfo();

        assertThat(info.getTitle()).isEqualTo("ERP API");
        assertThat(info.getDescription()).isEqualTo("API REST do ERP SaaS.");
        assertThat(info.getVersion()).isEqualTo("1.0.0");

        assertThat(info.getContact()).isNotNull();
        assertThat(info.getContact().getName()).isEqualTo("Time Backend");
        assertThat(info.getContact().getEmail()).isNull();

        assertThat(info.getLicense()).isNull();
    }
}