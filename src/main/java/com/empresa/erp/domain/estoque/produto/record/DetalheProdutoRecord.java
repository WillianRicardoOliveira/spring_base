package com.empresa.erp.domain.estoque.produto.record;

import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;

public record DetalheProdutoRecord(	
		Long id,
		String nome,
		String descricao,
		Integer quantidade,
		Integer minimo,
		Integer maximo
		) {
	
	public DetalheProdutoRecord(ProdutoModel dados) {
		
		this(	
				dados.getId(),
				dados.getNome(),
				dados.getDescricao(),
				dados.getQuantidade(),
				dados.getMinimo(),
				dados.getMaximo()
		);
				
	}

}