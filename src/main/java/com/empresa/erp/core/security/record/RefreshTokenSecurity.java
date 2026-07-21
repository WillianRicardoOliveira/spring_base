package com.empresa.erp.core.security.record;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenSecurity(
        @NotBlank(message = "{refresh.token.obrigatorio}")
        String refreshToken
) {
}