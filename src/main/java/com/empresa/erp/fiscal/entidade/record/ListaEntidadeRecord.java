package com.empresa.erp.fiscal.entidade.record;

import com.empresa.erp.fiscal.entidade.model.EntidadeModel;

public record ListaEntidadeRecord(
		Long id,
		String nomeCompleto,
		Boolean ativado
) {
	
	public ListaEntidadeRecord(EntidadeModel dados) {
		this(
			dados.getId(),
			dados.getNomeCompleto(),
			dados.getAtivado()
		);
	}	

}
