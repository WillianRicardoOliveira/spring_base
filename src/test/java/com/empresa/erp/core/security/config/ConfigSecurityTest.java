package com.empresa.erp.core.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
}