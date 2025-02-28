package com.empresa.erp.fiscal.entidade.record;

import com.empresa.erp.fiscal.entidade.model.EntidadeModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public record ListaEntidadeRecord(
		Long id,
		String nomeCompleto,
		StatusEnum status
) {
	
	public ListaEntidadeRecord(EntidadeModel dados) {
		this(
			dados.getId(),
			dados.getNomeCompleto(),
			dados.getStatus()
		);
	}	

}
