package home.office.spring.domain.estoque.produto.record;

import home.office.spring.domain.estoque.produto.model.ProdutoModel;

public record ListaProdutoRecord(
		Long id,
		String nome,
		Integer quantidade,
		Integer minimo,
		Integer maximo
) {
	
	public ListaProdutoRecord(ProdutoModel dados) {
		this(
				dados.getId(),
				dados.getNome(),
				dados.getQuantidade(),
				dados.getMinimo(),
				dados.getMaximo()
			);
	}	

}
