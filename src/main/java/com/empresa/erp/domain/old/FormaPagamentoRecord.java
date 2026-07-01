package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotBlank;

public record FormaPagamentoRecord(
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
