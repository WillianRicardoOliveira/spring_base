package home.office.spring.domain.fiscal.setorAtividade.record;

import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;

public record DetalheSetorAtividadeRecord(
		Long id,
		String nome
) {
		
	public DetalheSetorAtividadeRecord(SetorAtividadeModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
				
	}

}