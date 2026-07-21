package com.empresa.erp.core.security.record;

public record TokenGeradoSecurity(
        String token,
        String jti
) {
}