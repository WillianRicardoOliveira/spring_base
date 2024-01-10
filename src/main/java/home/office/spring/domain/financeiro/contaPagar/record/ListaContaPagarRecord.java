package home.office.spring.domain.financeiro.contaPagar.record;

import java.math.BigDecimal;

import home.office.spring.domain.financeiro.contaPagar.model.ContaPagarModel;

public record ListaContaPagarRecord(				
		Long id,
		String fornecedor,
		String subCategoriaConta,
		BigDecimal valor,
		Integer parcelar,
		String statusPagamento,
		String formaPagamento
) {
	
	public ListaContaPagarRecord(ContaPagarModel dados) {
		this(
				dados.getId(),
				dados.getFornecedor().getNome(),
				dados.getSubCategoriaConta().getNome(),
				dados.getValor(),
				dados.getParcelas(),
				dados.getStatusPagamento().getNome(),
				dados.getFormaPagamento().getNome()
		);
	}	
	
}
