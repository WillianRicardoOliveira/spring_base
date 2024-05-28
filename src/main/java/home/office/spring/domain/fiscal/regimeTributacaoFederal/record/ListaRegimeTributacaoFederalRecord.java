package home.office.spring.domain.fiscal.regimeTributacaoFederal.record;

import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

public record ListaRegimeTributacaoFederalRecord(				
		Long id,
		String nome
) {
	
	public ListaRegimeTributacaoFederalRecord(RegimeTributacaoFederalModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
		
	}	

}
