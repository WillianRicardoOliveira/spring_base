package com.empresa.erp.domain.old;

public record ListaBancoRecord(				
		Long id,
		String nome
) {
	
	public ListaBancoRecord(BancoModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
