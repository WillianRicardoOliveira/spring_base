package com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record;

import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;

public record ListaStatusPagamentoRecord(				
		Long id,
		String nome
) {
	
	public ListaStatusPagamentoRecord(StatusPagamentoModel dados) {
		this(
				dados.getId(),
				dados.getNome()
		);
	}	
	
}
