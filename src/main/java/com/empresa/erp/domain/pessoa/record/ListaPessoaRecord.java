package com.empresa.erp.domain.pessoa.record;

import com.empresa.erp.domain.pessoa.model.PessoaModel;

public record ListaPessoaRecord(
	Long id,
	String nome
) {
	
	public ListaPessoaRecord(PessoaModel dados) {
		this(dados.getId(), dados.getNome());
	}	

}
