package home.office.spring.domain.financeiro.contaPagar.record;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;

public record ListaContaPagarRecord(				
		Long id,
		String nome
) {
	
	public ListaContaPagarRecord(CategoriaContaModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
