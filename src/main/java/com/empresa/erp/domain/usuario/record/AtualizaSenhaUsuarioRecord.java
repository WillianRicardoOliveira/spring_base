package com.empresa.erp.domain.usuario.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaSenhaUsuarioRecord(
    @NotNull
    Long id,
    @NotBlank(message = "{usuario.senha.obrigatorio}")
    String senha
) {}