package com.empresa.erp.domain.old;

public record ListaCategoriaContaRecord(				
		Long id,
		String nome
) {
	
	public ListaCategoriaContaRecord(CategoriaContaModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
