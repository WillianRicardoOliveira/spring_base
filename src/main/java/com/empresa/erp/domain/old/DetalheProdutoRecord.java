package com.empresa.erp.domain.old;

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