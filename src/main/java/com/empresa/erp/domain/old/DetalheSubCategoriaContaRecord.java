package com.empresa.erp.domain.old;

public record DetalheSubCategoriaContaRecord(	
			Long id,
			String nome
			) {
		
	public DetalheSubCategoriaContaRecord(SubCategoriaContaModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}