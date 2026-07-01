package com.empresa.erp.domain.old;

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