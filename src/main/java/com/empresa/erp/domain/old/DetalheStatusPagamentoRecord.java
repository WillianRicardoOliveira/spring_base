package com.empresa.erp.domain.old;

public record DetalheStatusPagamentoRecord(	
			Long id,
			String nome
			) {
		
	public DetalheStatusPagamentoRecord(StatusPagamentoModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}