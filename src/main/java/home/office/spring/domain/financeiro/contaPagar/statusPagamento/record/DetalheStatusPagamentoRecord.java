package home.office.spring.domain.financeiro.contaPagar.statusPagamento.record;

import home.office.spring.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;

	public record DetalheStatusPagamentoRecord(	
			Long id,
			String nome
			) {
		
	public DetalheStatusPagamentoRecord(StatusPagamentoModel dados) {
		
			this(	
					dados.getId(),
					dados.getNome()
			);
				
	}

}