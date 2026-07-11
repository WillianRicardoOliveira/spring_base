package com.empresa.erp.core.security.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginSecurity(
    @NotBlank(message = "{usuario.email.obrigatorio}")
    @Email(message = "{usuario.email.invalido}")
    String email,

    @NotBlank(message = "{usuario.senha.obrigatorio}")
    String senha
) {}