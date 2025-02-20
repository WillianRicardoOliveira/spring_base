package com.empresa.erp.domain.financeiro.contaPagar.banco.conta.record;

import com.empresa.erp.domain.financeiro.contaPagar.banco.conta.model.ContaModel;
import com.empresa.erp.domain.financeiro.contaPagar.banco.record.DetalheBancoRecord;

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