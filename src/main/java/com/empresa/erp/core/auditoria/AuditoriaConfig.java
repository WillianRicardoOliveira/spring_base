package com.empresa.erp.core.auditoria;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import com.empresa.erp.core.security.model.UsuarioAutenticado;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditoriaConfig {

    @Bean
    AuditorAware<Long> auditorProvider() {
        return () -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
                return Optional.of(usuarioAutenticado.getId());
            }

            return Optional.empty();
        };
    }
}