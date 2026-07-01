package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotBlank;

public record CategoriaContaRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
