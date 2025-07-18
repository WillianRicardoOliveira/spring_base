package com.empresa.erp.modulos.fiscal.record;

import com.empresa.erp.domain.fiscal.endereco.record.DetalheEnderecoRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import com.empresa.erp.modulos.fiscal.model.EntidadeModel;
import com.empresa.erp.padrao.constant.StatusEnum;

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
		String emailNfe,
		String emailComercial,
		String primeiroTelefone,
		String segundoTelefone,
		Boolean nacional,
		DetalheEntidadeRecord matriz,
		StatusEnum status
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
			dados.getEmailNfe(),
			dados.getEmailComercial(),
			dados.getPrimeiroTelefone(),
			dados.getSegundoTelefone(),
			dados.getNacional(),
			dados.getMatriz() != null ? new DetalheEntidadeRecord(dados.getMatriz()) : null,			
			dados.getStatus()
		);		
	}

}