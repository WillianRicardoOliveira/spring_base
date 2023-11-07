package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.compraItem.model.CompraItemModel;
import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;

	public record DetalheCompraItemRecord(	
			
			DetalheFornecedorRecord fornecedor,
			DetalheProdutoRecord produto,
			Integer quantidade,
			BigDecimal valor,
			BigDecimal total,
			Boolean ativo		
			) {
		
	public DetalheCompraItemRecord(CompraItemModel dados) {
		
			this(	
					new DetalheFornecedorRecord(dados.getFornecedor()),
					new DetalheProdutoRecord(dados.getProduto()),
					dados.getQuantidade(),
					dados.getValor(),
					dados.getTotal(),
					dados.getAtivo()
			);
				
	}
	
}