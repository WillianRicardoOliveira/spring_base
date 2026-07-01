package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotBlank;

public record BancoRecord(		
		@NotBlank(message = "{nome.obrigatorio}")		
		String nome
) {}
