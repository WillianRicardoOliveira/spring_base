package com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.record;

import jakarta.validation.constraints.NotNull;

public record AtualizaTipoMovimentacaoRecord(
		@NotNull
		Long id,
		@NotNull(message = "{nome.obrigatorio}")
		String nome
) {}
