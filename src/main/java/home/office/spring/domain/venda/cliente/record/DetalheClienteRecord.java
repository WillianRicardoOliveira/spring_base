package home.office.spring.domain.venda.cliente.record;

import home.office.spring.domain.venda.cliente.model.ClienteModel;

public record DetalheClienteRecord(	
			Long id,
			String razaoSocial,
			String cnpj,
			String inscricaoEstadual,
			Boolean ativo	
		) {
		
	public DetalheClienteRecord(ClienteModel dados) {
		this(
			dados.getId(),
			dados.getRazaoSocial(),
			dados.getCnpj(),
			dados.getInscricaoEstadual(),
			dados.getAtivo()
		);
	}
}