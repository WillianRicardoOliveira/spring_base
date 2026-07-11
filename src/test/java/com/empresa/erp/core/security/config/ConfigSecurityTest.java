package com.empresa.erp.core.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.security.filter.FilterSecurity;

class ConfigSecurityTest {

    @Test
    @DisplayName("Deve estar configurado como security configuration")
    void deveEstarConfiguradoComoSecurityConfiguration() {
        assertThat(ConfigSecurity.class).hasAnnotation(Configuration.class);
        assertThat(ConfigSecurity.class).hasAnnotation(EnableWebSecurity.class);

        var enableMethodSecurity = ConfigSecurity.class.getAnnotation(EnableMethodSecurity.class);

        assertThat(enableMethodSecurity).isNotNull();
        assertThat(enableMethodSecurity.prePostEnabled()).isTrue();
        assertThat(enableMethodSecurity.securedEnabled()).isTrue();
    }

    @Test
    @DisplayName("Deve obter AuthenticationManager da configuracao")
    void deveObterAuthenticationManagerDaConfiguracao() throws Exception {
        var filter = org.mockito.Mockito.mock(FilterSecurity.class);
        var configuration = org.mockito.Mockito.mock(AuthenticationConfiguration.class);
        var authenticationManager = org.mockito.Mockito.mock(AuthenticationManager.class);

        var configSecurity = new ConfigSecurity(filter);

        when(configuration.getAuthenticationManager()).thenReturn(authenticationManager);

        var resultado = configSecurity.authenticationManager(configuration);

        assertThat(resultado).isEqualTo(authenticationManager);

        verify(configuration).getAuthenticationManager();
    }

    @Test
    @DisplayName("Deve criar PasswordEncoder BCrypt")
    void deveCriarPasswordEncoderBCrypt() {
        var filter = org.mockito.Mockito.mock(FilterSecurity.class);
        var configSecurity = new ConfigSecurity(filter);

        var passwordEncoder = configSecurity.passwordEncoder();

        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(passwordEncoder.matches("123456", passwordEncoder.encode("123456"))).isTrue();
    }

    @Test
    @DisplayName("Deve manter Swagger privado por padrao")
    void deveManterSwaggerPrivadoPorPadrao() {
        var filter = org.mockito.Mockito.mock(FilterSecurity.class);
        var configSecurity = new ConfigSecurity(filter);

        var swaggerPublic = ReflectionTestUtils.getField(configSecurity, "swaggerPublic");

        assertThat(swaggerPublic).isEqualTo(false);
    }

    @Test
    @DisplayName("Deve configurar propriedade do Swagger como falso por padrao")
    void deveConfigurarPropriedadeDoSwaggerComoFalsoPorPadrao() throws Exception {
        var campo = ConfigSecurity.class.getDeclaredField("swaggerPublic");

        var value = campo.getAnnotation(Value.class);

        assertThat(value).isNotNull();
        assertThat(value.value()).isEqualTo("${app.security.swagger-public:false}");
    }

    @Test
    @DisplayName("Deve permitir habilitar Swagger publico por configuracao")
    void devePermitirHabilitarSwaggerPublicoPorConfiguracao() {
        var filter = org.mockito.Mockito.mock(FilterSecurity.class);
        var configSecurity = new ConfigSecurity(filter);

        ReflectionTestUtils.setField(configSecurity, "swaggerPublic", true);

        var swaggerPublic = ReflectionTestUtils.getField(configSecurity, "swaggerPublic");

        assertThat(swaggerPublic).isEqualTo(true);
    }

    @Test
    @DisplayName("Deve manter Swagger privado no application properties")
    void deveManterSwaggerPrivadoNoApplicationProperties() throws Exception {
        var properties = carregarProperties("application.properties");

        assertThat(properties.getProperty("app.security.swagger-public")).isEqualTo("false");
    }

    @Test
    @DisplayName("Deve permitir Swagger publico no profile dev")
    void devePermitirSwaggerPublicoNoProfileDev() throws Exception {
        var properties = carregarProperties("application-dev.properties");

        assertThat(properties.getProperty("app.security.swagger-public")).isEqualTo("true");
        assertThat(properties.getProperty("springdoc.api-docs.enabled")).isEqualTo("true");
        assertThat(properties.getProperty("springdoc.swagger-ui.enabled")).isEqualTo("true");
    }

    @Test
    @DisplayName("Deve desabilitar Swagger no profile prod")
    void deveDesabilitarSwaggerNoProfileProd() throws Exception {
        var properties = carregarProperties("application-prod.properties");

        assertThat(properties.getProperty("app.security.swagger-public")).isEqualTo("false");
        assertThat(properties.getProperty("springdoc.api-docs.enabled")).isEqualTo("false");
        assertThat(properties.getProperty("springdoc.swagger-ui.enabled")).isEqualTo("false");
    }

    private Properties carregarProperties(String arquivo) throws Exception {
        var properties = new Properties();
        var resource = new ClassPathResource(arquivo);

        try (var inputStream = resource.getInputStream()) {
            properties.load(inputStream);
        }

        return properties;
    }
}