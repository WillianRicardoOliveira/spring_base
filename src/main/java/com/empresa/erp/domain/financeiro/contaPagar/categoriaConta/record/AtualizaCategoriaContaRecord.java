package com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaCategoriaContaRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
