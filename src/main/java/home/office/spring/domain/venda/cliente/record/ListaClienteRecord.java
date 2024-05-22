package home.office.spring.domain.venda.cliente.record;

import home.office.spring.domain.endereco.record.DetalheEnderecoRecord;
import home.office.spring.domain.venda.cliente.model.ClienteModel;

public record ListaClienteRecord(				
			Long id,
			String razaoSocial,
			String cnpj,
			String inscricaoEstadual,
			DetalheEnderecoRecord endereco,
			Boolean ativo
		) {
	
	public ListaClienteRecord(ClienteModel dados) {
		this(
				dados.getId(), 
				dados.getRazaoSocial(),
				dados.getCnpj(),
				dados.getInscricaoEstadual(),
				new DetalheEnderecoRecord(dados.getEndereco()),
				dados.getAtivo()
		);
	}
}
