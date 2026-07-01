package com.empresa.erp.domain.old;

public record DetalheCategoriaContaRecord(	
			Long id,
			String nome
			) {
		
	public DetalheCategoriaContaRecord(CategoriaContaModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}