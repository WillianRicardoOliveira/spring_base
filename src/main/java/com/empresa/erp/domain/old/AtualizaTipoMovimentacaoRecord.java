package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotNull;

public record AtualizaTipoMovimentacaoRecord(
		@NotNull
		Long id,
		@NotNull(message = "{nome.obrigatorio}")
		String nome
) {}
