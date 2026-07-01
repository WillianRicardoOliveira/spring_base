package com.empresa.erp.domain.old;

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