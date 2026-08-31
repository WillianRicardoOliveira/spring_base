package com.empresa.erp.core.bootstrap.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BootstrapPropertiesTest {

    @Test
    @DisplayName(
            "Deve preservar as configurações do bootstrap"
    )
    void devePreservarAsConfiguracoesDoBootstrap() {
        var properties =
                criarProperties(true);

        assertThat(properties.enabled())
                .isTrue();

        assertThat(properties.organizationName())
                .isEqualTo(
                        "Organização Principal"
                );

        assertThat(
                properties.organizationAdminEmail()
        ).isEqualTo(
                "admin.organizacao@teste.com"
        );

        assertThat(
                properties.organizationAdminPassword()
        ).isEqualTo(
                "SenhaOrganizacao@123"
        );

        assertThat(
                properties.platformAdminEmail()
        ).isEqualTo(
                "admin.plataforma@teste.com"
        );

        assertThat(
                properties.platformAdminPassword()
        ).isEqualTo(
                "SenhaPlataforma@123"
        );
    }

    @Test
    @DisplayName(
            "Deve permitir bootstrap desabilitado sem configurações"
    )
    void devePermitirBootstrapDesabilitadoSemConfiguracoes() {
        var properties =
                new BootstrapProperties(
                        false,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertThat(properties.enabled())
                .isFalse();

        assertThat(properties.organizationName())
                .isNull();

        assertThat(
                properties.organizationAdminEmail()
        ).isNull();

        assertThat(
                properties.organizationAdminPassword()
        ).isNull();

        assertThat(
                properties.platformAdminEmail()
        ).isNull();

        assertThat(
                properties.platformAdminPassword()
        ).isNull();
    }

    @Test
    @DisplayName(
            "Não deve expor dados sensíveis no toString"
    )
    void naoDeveExporDadosSensiveisNoToString() {
        var properties =
                criarProperties(true);

        String texto =
                properties.toString();

        assertThat(texto)
                .contains(
                        "enabled=true",
                        "organizationName=****",
                        "organizationAdminEmail=****",
                        "organizationAdminPassword=****",
                        "platformAdminEmail=****",
                        "platformAdminPassword=****"
                );

        assertThat(texto)
                .doesNotContain(
                        "Organização Principal",
                        "admin.organizacao@teste.com",
                        "SenhaOrganizacao@123",
                        "admin.plataforma@teste.com",
                        "SenhaPlataforma@123"
                );
    }

    @Test
    @DisplayName(
            "Deve informar no toString quando bootstrap está desabilitado"
    )
    void deveInformarNoToStringQuandoBootstrapEstaDesabilitado() {
        var properties =
                criarProperties(false);

        assertThat(properties.toString())
                .contains(
                        "enabled=false"
                )
                .doesNotContain(
                        "admin.organizacao@teste.com",
                        "SenhaOrganizacao@123",
                        "admin.plataforma@teste.com",
                        "SenhaPlataforma@123"
                );
    }

    private BootstrapProperties criarProperties(
            boolean enabled
    ) {
        return new BootstrapProperties(
                enabled,
                "Organização Principal",
                "admin.organizacao@teste.com",
                "SenhaOrganizacao@123",
                "admin.plataforma@teste.com",
                "SenhaPlataforma@123"
        );
    }
}