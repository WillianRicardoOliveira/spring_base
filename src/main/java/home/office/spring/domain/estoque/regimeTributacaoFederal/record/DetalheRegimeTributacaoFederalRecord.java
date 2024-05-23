package home.office.spring.domain.estoque.regimeTributacaoFederal.record;

import home.office.spring.domain.estoque.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

public record DetalheRegimeTributacaoFederalRecord(
		Long id,
		String nome
) {
		
	public DetalheRegimeTributacaoFederalRecord(RegimeTributacaoFederalModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
				
	}

}