package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotNull;

public record MovimentacaoRecord(
		@NotNull(message = "{tipo_movimentacao.obrigatorio}")
		DetalheTipoMovimentacaoRecord tipoMovimentacao,
		Long compra,
		@NotNull(message = "{produto.obrigatorio}")
		DetalheProdutoRecord produto,
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer quantidade
) {}
