package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;

	public record DetalheSubCategoriaContaRecord(	
			Long id,
			String nome
			) {
		
	public DetalheSubCategoriaContaRecord(SubCategoriaContaModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}