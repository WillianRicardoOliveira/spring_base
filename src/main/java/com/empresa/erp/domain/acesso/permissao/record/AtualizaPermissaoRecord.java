package com.empresa.erp.domain.acesso.permissao.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaPermissaoRecord(
    @NotNull
    Long id,
    @NotBlank(message = "{permissao.nome.obrigatorio}")
    String nome,
    @NotBlank(message = "{permissao.chave.obrigatoria}")
    String chave,
    String descricao
) {}