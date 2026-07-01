package com.empresa.erp.domain.old;

public record ListaContaRecord(				
		Long id,		
		String banco,		
		String agencia,
		String conta,
		String digito,
		String pix
) {
	
	public ListaContaRecord(ContaModel dados) {
		this(
				dados.getId(),
				dados.getBanco().getNome(),
				dados.getAgencia(),
				dados.getConta(),
				dados.getDigito(),
				dados.getPix()
		);
	}	
	
}
