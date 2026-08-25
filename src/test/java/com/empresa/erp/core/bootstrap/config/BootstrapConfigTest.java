package com.empresa.erp.core.bootstrap.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class BootstrapConfigTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(
                            BootstrapConfig.class
                    );

    @Test
    @DisplayName(
            "Deve registrar as propriedades do bootstrap no contexto"
    )
    void deveRegistrarPropriedadesDoBootstrapNoContexto() {
        contextRunner.run(contexto -> {
            assertThat(contexto)
                    .hasNotFailed()
                    .hasSingleBean(
                            BootstrapProperties.class
                    );

            BootstrapProperties properties =
                    contexto.getBean(
                            BootstrapProperties.class
                    );

            assertThat(properties.enabled())
                    .isFalse();

            assertThat(properties.organizationName())
                    .isNull();

            assertThat(properties.organizationAdminEmail())
                    .isNull();

            assertThat(properties.organizationAdminPassword())
                    .isNull();

            assertThat(properties.platformAdminEmail())
                    .isNull();

            assertThat(properties.platformAdminPassword())
                    .isNull();
        });
    }

    @Test
    @DisplayName(
            "Deve vincular todas as configurações do bootstrap"
    )
    void deveVincularTodasAsConfiguracoesDoBootstrap() {
        contextRunner
                .withPropertyValues(
                        "app.bootstrap.enabled=true",
                        "app.bootstrap.organization-name=Organização Principal",
                        "app.bootstrap.organization-admin-email="
                                + "admin.organizacao@teste.com",
                        "app.bootstrap.organization-admin-password="
                                + "Organizacao@2026",
                        "app.bootstrap.platform-admin-email="
                                + "admin.plataforma@teste.com",
                        "app.bootstrap.platform-admin-password="
                                + "Plataforma@2026"
                )
                .run(contexto -> {
                    assertThat(contexto)
                            .hasNotFailed()
                            .hasSingleBean(
                                    BootstrapProperties.class
                            );

                    BootstrapProperties properties =
                            contexto.getBean(
                                    BootstrapProperties.class
                            );

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
                            "Organizacao@2026"
                    );

                    assertThat(
                            properties.platformAdminEmail()
                    ).isEqualTo(
                            "admin.plataforma@teste.com"
                    );

                    assertThat(
                            properties.platformAdminPassword()
                    ).isEqualTo(
                            "Plataforma@2026"
                    );
                });
    }
}