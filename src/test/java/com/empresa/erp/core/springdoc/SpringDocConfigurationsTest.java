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

        assertThat(info.getTitle()).isEqualTo("API Futuro");
        assertThat(info.getDescription()).isEqualTo("API Rest da aplicação Futuro");

        assertThat(info.getContact().getName()).isEqualTo("Time Backend");
        assertThat(info.getContact().getEmail()).isEqualTo("backend@gmail.com");

        assertThat(info.getLicense().getName()).isEqualTo("Apache 2.0");
        assertThat(info.getLicense().getUrl()).isEqualTo("http://home.office/api/licenca");
    }
}