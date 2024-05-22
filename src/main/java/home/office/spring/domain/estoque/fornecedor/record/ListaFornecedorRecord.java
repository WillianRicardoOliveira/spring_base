package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;

public record ListaFornecedorRecord(				
		Long id,
		String cnpj,
		String razaoSocial,
		String nomeFantasia
) {
	
	public ListaFornecedorRecord(FornecedorModel dados) {
		
		this(
				dados.getId(),
				dados.getCnpj(),
				dados.getRazaoSocial(),
				dados.getNomeFantasia()				
		);
		
	}	

}
