package com.empresa.erp.domain.estoque.compra.record;

import jakarta.validation.constraints.NotBlank;

public record CompraRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,
		@NotBlank(message = "{descricao.obrigatorio}")
		String descricao	
) {}
