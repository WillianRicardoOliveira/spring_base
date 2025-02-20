package com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.record;

import com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.model.TipoMovimentacaoModel;

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
