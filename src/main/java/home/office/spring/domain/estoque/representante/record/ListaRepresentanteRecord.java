package home.office.spring.domain.estoque.representante.record;

import home.office.spring.domain.estoque.representante.model.RepresentanteModel;

public record ListaRepresentanteRecord(				
		Long id,
		String nome,
		String celular,
		Boolean ativo
) {
	
	public ListaRepresentanteRecord(RepresentanteModel dados) {
		this(
				dados.getId(), 
				dados.getNome(), 
				dados.getCelular(),
				dados.getAtivo()
		);
	}	

}
