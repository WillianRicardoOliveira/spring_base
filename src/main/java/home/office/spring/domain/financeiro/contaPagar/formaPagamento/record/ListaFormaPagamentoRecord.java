package home.office.spring.domain.financeiro.contaPagar.formaPagamento.record;

import home.office.spring.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;

public record ListaFormaPagamentoRecord(				
		Long id,
		String nome
) {
	
	public ListaFormaPagamentoRecord(FormaPagamentoModel dados) {
		this(
				dados.getId(),
				dados.getNome()
		);
	}	
	
}
