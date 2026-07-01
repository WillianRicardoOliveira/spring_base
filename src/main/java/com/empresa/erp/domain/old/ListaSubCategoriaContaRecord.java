package com.empresa.erp.domain.old;

public record ListaSubCategoriaContaRecord(				
		Long id,
		String nome
) {
	
	public ListaSubCategoriaContaRecord(SubCategoriaContaModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
