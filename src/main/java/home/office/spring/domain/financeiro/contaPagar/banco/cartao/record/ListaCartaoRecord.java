package home.office.spring.domain.financeiro.contaPagar.banco.cartao.record;

import home.office.spring.domain.financeiro.contaPagar.banco.cartao.model.CartaoModel;

public record ListaCartaoRecord(				
		Long id,
		String formaPagamento,
		String numeroCartao,
		String validadeMes,
		String validadeAno
) {
	
	public ListaCartaoRecord(CartaoModel dados) {
		this(
				dados.getId(), 
				dados.getFormaPagamento().getNome(),
				dados.getNumeroCartao(),
				dados.getValidadeMes(),
				dados.getValidadeAno()
		);
	}	
	
}
