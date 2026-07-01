package com.empresa.erp.domain.old;

public record DetalheBancoRecord(	
			Long id,
			String nome
			) {
		
	public DetalheBancoRecord(BancoModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()				
			);
				
	}

}