package home.office.spring.domain.financeiro.contaPagar.banco.conta.record;

import home.office.spring.domain.financeiro.contaPagar.banco.conta.model.ContaModel;

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
