package com.empresa.erp.domain.old;

public record ListaCartaoRecord(				
		Long id,
		String formaPagamento,
		String numeroCartao,
		String validadeMes,
		String validadeAno
) {
	
	public ListaCartaoRecord(CartaoModel dados) {
		this(
				dados.getId(), 
				dados.getFormaPagamento().getNome(),
				dados.getNumeroCartao(),
				dados.getValidadeMes(),
				dados.getValidadeAno()
		);
	}	
	
}
