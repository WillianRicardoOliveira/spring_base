package home.office.spring.domain.cadastro.fiscal.entidade.record;

import home.office.spring.domain.cadastro.fiscal.entidade.model.EntidadeModel;
import home.office.spring.domain.fiscal.endereco.record.DetalheEnderecoRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;

public record DetalheEntidadeRecord(
		Long id,
		String nomeCompleto,
		String nomeFantasia,
		
		// Tipo de entidade
		
		String nummeroDocumento,
		String inscricaoEstadual,
		String inscricaoMunicipal,
		DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,
		DetalheSetorAtividadeRecord setorAtividade,
		DetalheEnderecoRecord endereco,
		Boolean ativo
) {
		
	public DetalheEntidadeRecord(EntidadeModel dados) {
		this(
			dados.getId(),
			dados.getNomeCompleto(),
			dados.getNomeFantasia(),	
			
			// Tipo de entidade
			
			dados.getNumeroDocumento(),
			dados.getInscricaoEstadual(),
			dados.getInscricaoMunicipal(),
			new DetalheRegimeTributacaoFederalRecord(dados.getRegimeTributacaoFederal()),
			new DetalheSetorAtividadeRecord(dados.getSetorAtividade()),
			new DetalheEnderecoRecord(dados.getEndereco()),
			dados.getAtivo()
		);		
	}

}