package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.estoque.setorAtividade.record.DetalheSetorAtividadeRecord;

public record DetalheFornecedorRecord(
		Long id,
		String cnpj,
		String razaoSocial,
		String nomeFantasia,
		String inscricaoEstadual,
		String inscricaoMunicipal,
		DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,
		DetalheSetorAtividadeRecord setorAtividade
) {
		
	public DetalheFornecedorRecord(FornecedorModel dados) {
		
		this(
				dados.getId(),
				dados.getCnpj(),
				dados.getRazaoSocial(),
				dados.getNomeFantasia(),
				dados.getInscricaoEstadual(),
				dados.getInscricaoMunicipal(),
				new DetalheRegimeTributacaoFederalRecord(dados.getRegimeTributacaoFederal()),
				new DetalheSetorAtividadeRecord(dados.getSetorAtividade())
		);
				
	}

}