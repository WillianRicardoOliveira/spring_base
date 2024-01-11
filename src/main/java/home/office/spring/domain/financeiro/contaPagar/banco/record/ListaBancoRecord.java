package home.office.spring.domain.financeiro.contaPagar.banco.record;

import home.office.spring.domain.financeiro.contaPagar.banco.model.BancoModel;

public record ListaBancoRecord(				
		Long id,
		String nome,
		String agencia,
		String conta,
		String digito,
		String pix
) {
	
	public ListaBancoRecord(BancoModel dados) {
		this(
				dados.getId(), 
				dados.getNome(),
				dados.getAgencia(),
				dados.getConta(),
				dados.getDigito(),		
				dados.getPix()
		);
	}	
	
}
