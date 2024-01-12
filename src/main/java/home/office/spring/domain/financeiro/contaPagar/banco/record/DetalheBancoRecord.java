package home.office.spring.domain.financeiro.contaPagar.banco.record;

import home.office.spring.domain.financeiro.contaPagar.banco.model.BancoModel;

	public record DetalheBancoRecord(	
			Long id,
			String nome
			) {
		
	public DetalheBancoRecord(BancoModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()				
			);
				
	}

}