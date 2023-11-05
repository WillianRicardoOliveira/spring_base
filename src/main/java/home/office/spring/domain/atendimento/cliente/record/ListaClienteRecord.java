package home.office.spring.domain.atendimento.cliente.record;

import home.office.spring.domain.atendimento.cliente.model.ClienteModel;

public record ListaClienteRecord(				
		Long id,
		String nome,
		String telefone,
		Boolean ativo
) {
	
	public ListaClienteRecord(ClienteModel dados) {
		this(
				dados.getId(), 
				dados.getNome(),
				dados.getTelefone(),
				dados.getAtivo()
		);
	}	

}
