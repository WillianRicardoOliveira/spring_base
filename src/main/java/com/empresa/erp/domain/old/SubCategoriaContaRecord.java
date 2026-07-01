package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubCategoriaContaRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,		
		@NotNull(message = "{obrigatorio}")
		Long categoriaConta		
) {}
