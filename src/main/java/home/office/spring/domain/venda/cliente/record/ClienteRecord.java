package home.office.spring.domain.venda.cliente.record;

import home.office.spring.domain.endereco.record.DetalheEnderecoRecord;

public record ClienteRecord(			
		
		String razaoSocial,
		
		String cnpj,
		
		String inscricaoEstadual,
		
		DetalheEnderecoRecord endereco
		
) {}
