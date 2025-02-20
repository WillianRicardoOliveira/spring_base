package com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaSubCategoriaContaRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
