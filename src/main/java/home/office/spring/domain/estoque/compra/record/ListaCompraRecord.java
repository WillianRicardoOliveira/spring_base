package home.office.spring.domain.estoque.compra.record;

import home.office.spring.domain.estoque.compra.constante.Status;
import home.office.spring.domain.estoque.compra.model.CompraModel;

public record ListaCompraRecord(				
		Long id,
		String descricao,
		Status status,
		Boolean ativo
) {
	
	public ListaCompraRecord(CompraModel dados) {
		this(
				dados.getId(), 
				dados.getDescricao(), 
				dados.getStatus(),
				dados.getAtivo()
		);
	}	

}
