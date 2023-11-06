package home.office.spring.domain.estoque.compra.record;

import home.office.spring.domain.estoque.compra.constante.Status;
import home.office.spring.domain.estoque.compra.model.CompraModel;

	public record DetalheCompraRecord(	
			String descricao,
			Status status,
			String data,
			Boolean ativo		
			) {
		
	public DetalheCompraRecord(CompraModel dados) {
		
			this(	
					dados.getDescricao(),
					dados.getStatus(),
					dados.getData(),
					dados.getAtivo()
			);
				
	}

}