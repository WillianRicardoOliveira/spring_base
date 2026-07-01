package com.empresa.erp.domain.old;

public record ListaSetorAtividadeRecord(				
		Long id,
		String nome
) {
	
	public ListaSetorAtividadeRecord(SetorAtividadeModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
		
	}	

}
