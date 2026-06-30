package com.empresa.erp.domain.acesso.perfil.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaPerfilRecord(
    @NotNull
    Long id,
    @NotBlank(message = "{perfil.nome.obrigatorio}")
    String nome,
    String descricao
) {}