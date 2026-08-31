package com.empresa.erp.core.bootstrap.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.empresa.erp.core.bootstrap.config.BootstrapProperties;
import com.empresa.erp.core.bootstrap.service.BootstrapService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootstrapRunner implements ApplicationRunner {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BootstrapRunner.class);

    private final BootstrapProperties properties;

    private final BootstrapService bootstrapService;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.enabled()) {
            return;
        }

        boolean provisionado =
                bootstrapService.provisionar();

        if (provisionado) {
            LOGGER.warn(
                    "Bootstrap inicial concluido. "
                            + "Desabilite BOOTSTRAP_ENABLED "
                            + "e remova as credenciais do ambiente."
            );

            return;
        }

        LOGGER.info(
                "Bootstrap ignorado: "
                        + "a instalacao ja esta provisionada."
        );
    }
}