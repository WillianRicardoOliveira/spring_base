package home.office.spring.domain.fiscal.regimeTributacaoFederal.record;

import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

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