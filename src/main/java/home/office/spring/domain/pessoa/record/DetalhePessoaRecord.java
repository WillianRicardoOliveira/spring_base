package home.office.spring.domain.pessoa.record;

import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.pessoa.model.PessoaModel;

public record DetalhePessoaRecord(	
		//Long id,
		String nome,
		String nascimento,
		String genero,
		String cpf,
		String telefone
		//EnderecoRecord endereco
		//UsuarioModel usuario,
		//Boolean aceitarTermos,
		//TipoPessoa tipoPessoa,
		//Boolean ativo
		) {
	
	public DetalhePessoaRecord(PessoaModel dados) {
		
		this(	//dados.getId(),
				dados.getNome(),
				dados.getNascimento(),
				dados.getGenero(),
				dados.getCpf(),
				dados.getTelefone()				
				//new EnderecoRecord(dados.getEndereco())
				//dados.getUsuario(),
				//dados.getAceitarTermos(),
				//dados.getTipoPessoa(),
				//dados.getAtivo()
		);
				
	}

}