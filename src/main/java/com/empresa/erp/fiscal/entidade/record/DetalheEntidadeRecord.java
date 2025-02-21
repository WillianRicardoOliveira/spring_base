package com.empresa.erp.fiscal.entidade.record;

import com.empresa.erp.domain.fiscal.endereco.record.DetalheEnderecoRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import com.empresa.erp.fiscal.entidade.model.EntidadeModel;

public record DetalheEntidadeRecord(	
		Long id,
		Boolean pessoaJuridica,
		String nomeCompleto,
		String numeroDocumento,
		String inscricaoEstadual,
		String inscricaoMunicipal,
		Boolean cliente,	
		Boolean fornecedor,	
		Boolean parceiro,	
		Boolean transportador,
		DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,
		DetalheSetorAtividadeRecord setorAtividade,
		DetalheEnderecoRecord endereco,
		String contatoPrincipal,
		String emailNFe,
		String emailComercial,
		String telefonePrimeiro,
		String telefoneSegundo,
		String observacao,
		String motivoInativacao,
		Boolean nacional,
		DetalheEntidadeRecord matriz,
		Boolean ativado,
		Boolean removido
) {
		
	public DetalheEntidadeRecord(EntidadeModel dados) {
		this(
			dados.getId(),
			dados.getPessoaJuridica(),
			dados.getNomeCompleto(),			
			dados.getNumeroDocumento(),
			dados.getInscricaoEstadual(),
			dados.getInscricaoMunicipal(),
			dados.getCliente(),	
			dados.getFornecedor(),	
			dados.getParceiro(),	
			dados.getTransportador(),
			new DetalheRegimeTributacaoFederalRecord(dados.getRegimeTributacaoFederal()),
			new DetalheSetorAtividadeRecord(dados.getSetorAtividade()),
			new DetalheEnderecoRecord(dados.getEndereco()),
			dados.getContatoPrincipal(),
			dados.getEmailNFe(),
			dados.getEmailComercial(),
			dados.getTelefonePrimeiro(),
			dados.getTelefoneSegundo(),
			dados.getObservacao(),
			dados.getMotivoInativacao(),
			dados.getNacional(),
			new DetalheEntidadeRecord(dados.getMatriz()),			
			dados.getAtivado(),
			dados.getRemovido()
		);		
	}

}