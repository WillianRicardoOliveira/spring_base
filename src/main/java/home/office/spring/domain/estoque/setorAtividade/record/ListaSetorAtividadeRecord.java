package home.office.spring.domain.estoque.setorAtividade.record;

import home.office.spring.domain.estoque.setorAtividade.model.SetorAtividadeModel;

public record ListaSetorAtividadeRecord(				
		Long id,
		String nome
) {
	
	public ListaSetorAtividadeRecord(SetorAtividadeModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
		
	}	

}
