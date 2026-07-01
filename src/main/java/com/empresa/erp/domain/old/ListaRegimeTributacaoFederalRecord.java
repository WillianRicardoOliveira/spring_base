package com.empresa.erp.domain.old;

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
