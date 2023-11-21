package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;

public record ListaFornecedorRecord(				
		Long id,
		String cnpj,
		String nome,
		String telefone		
) {
	
	
	public ListaFornecedorRecord(FornecedorModel dados) {
		this(
				dados.getId(), 
				dados.cnpjFormatado(),				 
				dados.getNome(),
				dados.telefoneFormatado()				
		);
	}	

}
