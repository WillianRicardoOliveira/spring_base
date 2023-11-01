package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.pessoa.model.PessoaModel;

public record ListaFornecedorRecord(				
		Long id,
		String cnpj,
		String nome,
		Boolean ativo
) {
	
	public ListaFornecedorRecord(FornecedorModel dados) {
		this(
				dados.getId(), 
				dados.getCnpj(), 
				dados.getNome(),
				dados.getAtivo()
		);
	}	

}
