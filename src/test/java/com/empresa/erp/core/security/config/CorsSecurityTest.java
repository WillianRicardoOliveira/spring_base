package com.empresa.erp.core.security.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

class CorsSecurityTest {

    @Test
    @DisplayName("Deve configurar CORS para frontend local")
    void deveConfigurarCorsParaFrontendLocal() {
        var corsSecurity = new CorsSecurity();
        var registry = new CorsRegistry();

        corsSecurity.addCorsMappings(registry);

        var configuracoes = obterConfiguracoesCors(registry);
        var config = configuracoes.get("/**");

        assertThat(config).isNotNull();
        assertThat(config.getAllowedOrigins()).containsExactly("http://localhost:4200");
        assertThat(config.getAllowedMethods())
                .containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(config.getAllowedHeaders()).containsExactly("*");
    }

    @SuppressWarnings("unchecked")
    private Map<String, CorsConfiguration> obterConfiguracoesCors(CorsRegistry registry) {
        return (Map<String, CorsConfiguration>) ReflectionTestUtils.invokeMethod(
                registry,
                "getCorsConfigurations"
        );
    }
}