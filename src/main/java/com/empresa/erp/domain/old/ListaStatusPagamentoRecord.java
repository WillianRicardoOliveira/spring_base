package com.empresa.erp.domain.old;

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
