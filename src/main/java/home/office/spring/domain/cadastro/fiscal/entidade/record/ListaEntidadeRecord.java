package home.office.spring.domain.cadastro.fiscal.entidade.record;

import home.office.spring.domain.cadastro.fiscal.entidade.model.EntidadeModel;

public record ListaEntidadeRecord(
		Long id,
		String nomeCompleto,
		Boolean ativo
) {
	
	public ListaEntidadeRecord(EntidadeModel dados) {
		this(
			dados.getId(),
			dados.getNomeCompleto(),
			dados.getAtivo()
		);
	}	

}
