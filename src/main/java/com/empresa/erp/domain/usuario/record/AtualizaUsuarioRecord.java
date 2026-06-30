package com.empresa.erp.domain.usuario.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaUsuarioRecord(
    @NotNull
    Long id,
    @NotBlank(message = "{usuario.email.obrigatorio}")
    @Email(message = "{usuario.email.invalido}")
    String email
) {}