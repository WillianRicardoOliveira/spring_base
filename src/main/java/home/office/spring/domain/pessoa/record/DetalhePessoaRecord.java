package home.office.spring.domain.pessoa.record;

import home.office.spring.domain.endereco.model.EnderecoModel;
import home.office.spring.domain.pessoa.constante.TipoPessoa;
import home.office.spring.domain.pessoa.model.PessoaModel;
import home.office.spring.domain.usuario.model.UsuarioModel;

public record DetalhePessoaRecord(	
		Long id,
		String nome,
		String nascimento,
		String genero,
		String cpf,
		String telefone,
		EnderecoModel endereco,
		UsuarioModel usuario,
		Boolean aceiteTermo,
		TipoPessoa tipoPessoa,
		Boolean ativo
		) {
	
	public DetalhePessoaRecord(PessoaModel dados) {
		
		this(	dados.getId(),
				dados.getNome(),
				dados.getNascimento(),
				dados.getGenero(),
				dados.getCpf(),
				dados.getTelefone(),				
				dados.getEndereco(),
				dados.getUsuario(),
				dados.getAceiteTermo(),
				dados.getTipoPessoa(),
				dados.getAtivo()
		);
				
	}

}