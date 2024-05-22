package home.office.spring.domain.endereco.record;

import home.office.spring.domain.endereco.model.EnderecoModel;

public record DetalheEnderecoRecord(	
			Long id
		) {
		
	/*
	 * 
	 */
	public DetalheEnderecoRecord(EnderecoModel dados) {
		this(	
			dados.getId()
		);			
	}
}