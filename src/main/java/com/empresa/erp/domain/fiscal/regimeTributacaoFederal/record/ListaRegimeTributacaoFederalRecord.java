package com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record;

import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

public record ListaRegimeTributacaoFederalRecord(				
		Long id,
		String nome
) {
	
	public ListaRegimeTributacaoFederalRecord(RegimeTributacaoFederalModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
		
	}	

}
