package com.empresa.erp.domain.old;

public record ListaFormaPagamentoRecord(				
		Long id,
		String nome
) {
	
	public ListaFormaPagamentoRecord(FormaPagamentoModel dados) {
		this(
				dados.getId(),
				dados.getNome()
		);
	}	
	
}
