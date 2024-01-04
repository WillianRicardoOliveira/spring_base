package home.office.spring.domain.financeiro.contaPagar.categoriaConta.record;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;

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
