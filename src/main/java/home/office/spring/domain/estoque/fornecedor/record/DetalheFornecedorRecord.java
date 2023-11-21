package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;

	public record DetalheFornecedorRecord(
			Long id,
			String cnpj,
			String nome,
			String telefone,
			String descricao,
			Boolean ativo		
			) {
		
	public DetalheFornecedorRecord(FornecedorModel dados) {
		
			this(
					dados.getId(),
					dados.getCnpj(),
					dados.getNome(),
					dados.getTelefone(),
					dados.getDescricao(),
					dados.getAtivo()
			);
				
	}

}