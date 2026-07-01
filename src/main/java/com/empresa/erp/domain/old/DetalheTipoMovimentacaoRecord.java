package com.empresa.erp.domain.old;

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