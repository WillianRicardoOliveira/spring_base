package com.empresa.erp.domain.old;

public record ListaTipoMovimentacaoRecord(				
		Long id,
		String nome
) {
	
	public ListaTipoMovimentacaoRecord(TipoMovimentacaoModel dados) {
		this(
				dados.getId(),
				dados.getNome()
		);
	}	
	
}
