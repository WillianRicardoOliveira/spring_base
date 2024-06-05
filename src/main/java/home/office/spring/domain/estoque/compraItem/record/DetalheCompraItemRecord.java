package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.compraItem.model.CompraItemModel;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;
import home.office.spring.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;

	public record DetalheCompraItemRecord(	
			Long id,
			DetalheFornecedorRecord fornecedor,
			DetalheProdutoRecord produto,
			Integer quantidade,
			BigDecimal valor,
			BigDecimal total
			) {
		
	public DetalheCompraItemRecord(CompraItemModel dados) {
		
			this(	
					dados.getId(),
					new DetalheFornecedorRecord(dados.getFornecedor()),
					new DetalheProdutoRecord(dados.getProduto()),
					dados.getQuantidade(),
					dados.getValor(),
					dados.getTotal()
			);
				
	}
	
}