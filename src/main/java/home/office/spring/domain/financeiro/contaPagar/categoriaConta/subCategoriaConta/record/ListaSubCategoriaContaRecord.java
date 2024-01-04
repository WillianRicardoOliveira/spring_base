package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;

public record ListaSubCategoriaContaRecord(				
		Long id,
		String nome
) {
	
	public ListaSubCategoriaContaRecord(SubCategoriaContaModel dados) {
		this(
				dados.getId(), 
				dados.getNome()
		);
	}	
	
}
