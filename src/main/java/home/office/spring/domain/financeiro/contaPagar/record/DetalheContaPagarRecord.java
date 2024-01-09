package home.office.spring.domain.financeiro.contaPagar.record;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;

	public record DetalheContaPagarRecord(	
			Long id,
			String nome
			) {
		
	public DetalheContaPagarRecord(CategoriaContaModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}