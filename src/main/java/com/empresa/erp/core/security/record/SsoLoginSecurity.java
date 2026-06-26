package com.empresa.erp.core.security.record;

import jakarta.validation.constraints.NotBlank;

public record SsoLoginSecurity(
    @NotBlank(message = "Token SSO e obrigatorio")
    String token
) {

}