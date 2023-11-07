package home.office.spring.domain.atendimento.cliente.record;

import home.office.spring.domain.atendimento.cliente.model.ClienteModel;

	public record DetalheClienteRecord(	
			String nome,
			String telefone,
			Boolean ativo		
			) {
		
	public DetalheClienteRecord(ClienteModel dados) {
		
			this(	
					dados.getNome(),
					dados.getTelefone(),
					dados.getAtivo()
			);
				
	}
	
	public DetalheClienteRecord() {
		
		this(null, null, null);
			
	}

}