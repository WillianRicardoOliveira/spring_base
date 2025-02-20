package com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record;

import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;

	public record DetalheFormaPagamentoRecord(	
			Long id,
			String nome
			) {
		
	public DetalheFormaPagamentoRecord(FormaPagamentoModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}