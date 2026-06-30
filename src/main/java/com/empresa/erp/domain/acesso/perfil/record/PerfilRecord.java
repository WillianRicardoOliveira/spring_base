package com.empresa.erp.domain.acesso.perfil.record;

import jakarta.validation.constraints.NotBlank;

public record PerfilRecord(
    @NotBlank(message = "{perfil.nome.obrigatorio}")
    String nome,
    String descricao
) {}