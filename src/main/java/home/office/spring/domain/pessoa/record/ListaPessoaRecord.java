package home.office.spring.domain.pessoa.record;

import home.office.spring.domain.pessoa.model.PessoaModel;

public record ListaPessoaRecord(
	Long id,
	String nome
) {
	
	public ListaPessoaRecord(PessoaModel dados) {
		this(dados.getId(), dados.getNome());
	}	

}
