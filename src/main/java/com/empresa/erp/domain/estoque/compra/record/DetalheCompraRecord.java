package com.empresa.erp.domain.estoque.compra.record;

import java.time.LocalDateTime;

import com.empresa.erp.domain.estoque.compra.constante.Status;
import com.empresa.erp.domain.estoque.compra.model.CompraModel;

	public record DetalheCompraRecord(	
			Long id,
			String descricao,
			Status status,
			LocalDateTime data
			) {
		
	public DetalheCompraRecord(CompraModel dados) {
		
			this(	
					dados.getId(),
					dados.getDescricao(),
					dados.getStatus(),
					dados.getData()
			);
				
	}
	
	public DetalheCompraRecord() {
		this(null, null, null, null);
	}

}