package com.empresa.erp.domain.old;

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