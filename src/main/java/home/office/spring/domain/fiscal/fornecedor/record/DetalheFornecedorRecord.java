package home.office.spring.domain.fiscal.fornecedor.record;

import home.office.spring.domain.fiscal.endereco.record.DetalheEnderecoRecord;
import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;

public record DetalheFornecedorRecord(
		
	Long id,
	
	Boolean tipo,		
	
	String nummeroDocumento,
	
	String razaoSocial,
	
	String nomeFantasia,
	
	String inscricaoEstadual,
	
	String inscricaoMunicipal,
	
	DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,
	
	DetalheSetorAtividadeRecord setorAtividade,
	
	DetalheEnderecoRecord endereco,
	
	Boolean ativo
			
) {
		
	public DetalheFornecedorRecord(FornecedorModel dados) {
		
		this(
		
			dados.getId(),
				
			dados.getTipo(),
			
			dados.getNumeroDocumento(),
			
			dados.getRazaoSocial(),
			
			dados.getNomeFantasia(),
			
			dados.getInscricaoEstadual(),
			
			dados.getInscricaoMunicipal(),
			
			new DetalheRegimeTributacaoFederalRecord(dados.getRegimeTributacaoFederal()),
			
			new DetalheSetorAtividadeRecord(dados.getSetorAtividade()),
				
			new DetalheEnderecoRecord(dados.getEndereco()),
			
			dados.getAtivo()
			
		);
				
	}

}