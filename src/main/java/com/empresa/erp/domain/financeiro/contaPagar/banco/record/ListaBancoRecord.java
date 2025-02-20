package com.empresa.erp.domain.financeiro.contaPagar.banco.record;

import com.empresa.erp.domain.financeiro.contaPagar.banco.model.BancoModel;

public record ListaBancoRecord(				
		Long id,
		String nome
) {
	
	public ListaBancoRecord(BancoModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
