package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.compraItem.model.CompraItemModel;
import home.office.spring.domain.estoque.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.estoque.produto.record.ListaProdutoRecord;

public record ListaCompraItemRecord(				
		Long id,
		ListaFornecedorRecord fornecedor,
		ListaProdutoRecord produto,
		Integer quantidade,
		BigDecimal valor,
		BigDecimal total,
		Boolean ativo	
) {
	
	public ListaCompraItemRecord(CompraItemModel dados) {
		this(
				dados.getId(),
				new ListaFornecedorRecord(dados.getFornecedor()),
				new ListaProdutoRecord(dados.getProduto()),
				dados.getQuantidade(),
				dados.getValor(),
				dados.getTotal(),
				dados.getAtivo()
		);
	}	
	
}
