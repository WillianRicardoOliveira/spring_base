package home.office.spring.domain.financeiro.contaPagar.categoriaConta.record;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;

	public record DetalheCategoriaContaRecord(	
			Long id,
			String nome
			) {
		
	public DetalheCategoriaContaRecord(CategoriaContaModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}