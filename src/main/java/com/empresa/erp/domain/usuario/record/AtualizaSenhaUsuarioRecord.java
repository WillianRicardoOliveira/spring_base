package com.empresa.erp.domain.usuario.record;

import com.empresa.erp.core.validation.SenhaForte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaSenhaUsuarioRecord(
    @NotNull
    Long id,

    @NotBlank(message = "{usuario.senha.obrigatorio}")
    @SenhaForte
    String senha
) {}