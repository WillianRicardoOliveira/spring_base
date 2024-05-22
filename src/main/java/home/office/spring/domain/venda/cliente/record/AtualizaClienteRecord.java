package home.office.spring.domain.venda.cliente.record;

import home.office.spring.domain.endereco.record.DetalheEnderecoRecord;
import jakarta.validation.constraints.NotNull;

public record AtualizaClienteRecord(
		@NotNull
		Long id,
		
		String razaoSocial,
		
		String cnpj,
		
		String inscricaoEstadual,
		
		DetalheEnderecoRecord endereco
		
) {}
