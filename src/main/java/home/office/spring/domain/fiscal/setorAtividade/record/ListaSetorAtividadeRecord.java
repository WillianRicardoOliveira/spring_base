package home.office.spring.domain.fiscal.setorAtividade.record;

import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;

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
