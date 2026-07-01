package com.empresa.erp.domain.old;

import java.time.LocalDateTime;

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