package com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record;

import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;

public record ListaCategoriaContaRecord(				
		Long id,
		String nome
) {
	
	public ListaCategoriaContaRecord(CategoriaContaModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
