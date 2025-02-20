package com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.record;

import com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.model.TipoMovimentacaoModel;

	public record DetalheTipoMovimentacaoRecord(	
			Long id,
			String nome
			) {
		
	public DetalheTipoMovimentacaoRecord(TipoMovimentacaoModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}
	
}