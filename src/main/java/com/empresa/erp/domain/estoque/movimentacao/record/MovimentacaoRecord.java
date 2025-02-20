package com.empresa.erp.domain.estoque.movimentacao.record;

import com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.record.DetalheTipoMovimentacaoRecord;
import com.empresa.erp.domain.estoque.produto.record.DetalheProdutoRecord;

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
