package com.empresa.erp.domain.estoque.compra.record;

import com.empresa.erp.domain.estoque.compra.constante.Status;
import com.empresa.erp.domain.estoque.compra.model.CompraModel;

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
