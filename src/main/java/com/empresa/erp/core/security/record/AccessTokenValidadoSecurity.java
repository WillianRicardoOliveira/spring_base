package com.empresa.erp.core.security.record;

public record AccessTokenValidadoSecurity(
        String subject,
        String jti
) {
}