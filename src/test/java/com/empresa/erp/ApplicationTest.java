package com.empresa.erp;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

class ApplicationTest {

    @Test
    @DisplayName("Deve iniciar aplicacao Spring Boot")
    void deveIniciarAplicacaoSpringBoot() {
        var args = new String[] { "--spring.profiles.active=test" };

        try (var springApplication = mockStatic(SpringApplication.class)) {
            Application.main(args);

            springApplication.verify(() -> SpringApplication.run(Application.class, args));
        }
    }
}