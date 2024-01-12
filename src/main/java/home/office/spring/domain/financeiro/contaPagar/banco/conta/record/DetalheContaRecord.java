package home.office.spring.domain.financeiro.contaPagar.banco.conta.record;

import home.office.spring.domain.financeiro.contaPagar.banco.conta.model.ContaModel;
import home.office.spring.domain.financeiro.contaPagar.banco.record.DetalheBancoRecord;

	public record DetalheContaRecord(	
			Long id,
			DetalheBancoRecord banco,
			String agencia,
			String conta,
			String digito,
			String pix
			) {
		
	public DetalheContaRecord(ContaModel dados) {
		
			this(	
					dados.getId(),					
					new DetalheBancoRecord(dados.getBanco()),					
					dados.getAgencia(),
					dados.getConta(),
					dados.getDigito(),		
					dados.getPix()					
			);
				
	}

}