package com.empresa.erp.domain.old;

public record ListaCompraRecord(				
		Long id,
		String descricao,
		Status status
) {
	
	public ListaCompraRecord(CompraModel dados) {
		this(
				dados.getId(), 
				dados.getDescricao(), 
				dados.getStatus()
		);
	}	

	public ListaCompraRecord() {
		this(null, null, null);
	}	
	
}
