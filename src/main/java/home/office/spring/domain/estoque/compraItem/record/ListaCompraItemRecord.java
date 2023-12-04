package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.compraItem.model.CompraItemModel;

public record ListaCompraItemRecord(				
		Long id,
		String fornecedor,
		String produto,
		Integer quantidade,
		BigDecimal valor,
		BigDecimal total
) {
	
	public ListaCompraItemRecord(CompraItemModel dados) {
		this(
				dados.getId(),
				dados.getFornecedor().getNome(),
				dados.getProduto().getNome(),
				dados.getQuantidade(),
				dados.getValor(),
				dados.getTotal()
		);
	}	
	
}
