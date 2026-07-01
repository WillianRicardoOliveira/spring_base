package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotNull;

public record TipoMovimentacaoRecord(
		@NotNull(message = "{nome.obrigatorio}")
		String nome
) {}
