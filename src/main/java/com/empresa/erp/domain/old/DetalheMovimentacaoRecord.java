package com.empresa.erp.domain.old;

import java.time.LocalDateTime;

	public record DetalheMovimentacaoRecord(
			Long id,
			DetalheTipoMovimentacaoRecord tipoMovimentacao,
			DetalheCompraRecord compra,
			DetalheProdutoRecord produto,
			Integer quantidade,
			Integer total,
			LocalDateTime data
			) {
		
	public DetalheMovimentacaoRecord(MovimentacaoModel dados) {
		
			this(	
					dados.getId(),
					new DetalheTipoMovimentacaoRecord(dados.getTipoMovimentacao()),
					dados.getCompra() != null ? new DetalheCompraRecord(dados.getCompra()) : new DetalheCompraRecord(),
					new DetalheProdutoRecord(dados.getProduto()),
					dados.getQuantidade(),
					dados.getTotal(),
					dados.getData()
			);
				
	}

}