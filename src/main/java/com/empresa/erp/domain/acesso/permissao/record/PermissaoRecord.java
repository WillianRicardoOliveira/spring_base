package com.empresa.erp.domain.acesso.permissao.record;

import jakarta.validation.constraints.NotBlank;

public record PermissaoRecord(
    @NotBlank(message = "{permissao.nome.obrigatorio}")
    String nome,
    @NotBlank(message = "{permissao.chave.obrigatoria}")
    String chave,
    String descricao
) {}