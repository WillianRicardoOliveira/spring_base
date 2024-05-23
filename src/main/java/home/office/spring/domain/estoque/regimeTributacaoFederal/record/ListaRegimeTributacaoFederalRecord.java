package home.office.spring.domain.estoque.regimeTributacaoFederal.record;

import home.office.spring.domain.estoque.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

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
