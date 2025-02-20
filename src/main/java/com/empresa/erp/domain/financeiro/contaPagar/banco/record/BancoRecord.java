package com.empresa.erp.domain.financeiro.contaPagar.banco.record;

import jakarta.validation.constraints.NotBlank;

public record BancoRecord(		
		@NotBlank(message = "{nome.obrigatorio}")		
		String nome
) {}
