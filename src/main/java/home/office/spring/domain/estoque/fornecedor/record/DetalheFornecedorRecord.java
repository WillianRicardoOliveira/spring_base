package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;

	public record DetalheFornecedorRecord(	
			String cnpj,
			String nome,
			Boolean ativo		
			) {
		
	public DetalheFornecedorRecord(FornecedorModel dados) {
		
			this(	
					dados.getCnpj(),
					dados.getNome(),	
					dados.getAtivo()
			);
				
	}

}