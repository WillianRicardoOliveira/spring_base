package home.office.spring.domain.estoque.produto.record;

import home.office.spring.domain.estoque.produto.model.ProdutoModel;

public record DetalheProdutoRecord(	
		String nome,
		String descricao,
		Integer quantidade,
		Integer minimo,
		Integer maximo,
		Boolean ativo
		) {
	
	public DetalheProdutoRecord(ProdutoModel dados) {
		
		this(	
				dados.getNome(),
				dados.getDescricao(),
				dados.getQuantidade(),
				dados.getMinimo(),
				dados.getMaximo(),
				dados.getAtivo()
		);
				
	}

}