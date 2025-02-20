package com.empresa.erp.domain.fiscal.setorAtividade.record;

import com.empresa.erp.domain.fiscal.setorAtividade.model.SetorAtividadeModel;

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
