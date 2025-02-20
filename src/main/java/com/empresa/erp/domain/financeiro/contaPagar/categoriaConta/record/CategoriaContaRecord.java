package com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record;

import jakarta.validation.constraints.NotBlank;

public record CategoriaContaRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
