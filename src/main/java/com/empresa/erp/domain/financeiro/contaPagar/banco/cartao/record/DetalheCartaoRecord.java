package com.empresa.erp.domain.financeiro.contaPagar.banco.cartao.record;

import com.empresa.erp.domain.financeiro.contaPagar.banco.cartao.model.CartaoModel;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;

	public record DetalheCartaoRecord(	
			Long id,
			DetalheFormaPagamentoRecord formaPagamento,
			String numeroCartao,
			String validadeMes,
			String validadeAno
			) {
		
	public DetalheCartaoRecord(CartaoModel dados) {
		
			this(	
					dados.getId(),
					new DetalheFormaPagamentoRecord(dados.getFormaPagamento()),
					dados.getNumeroCartao(),
					dados.getValidadeMes(),
					dados.getValidadeAno()
			);
				
	}

}