package home.office.spring.domain.estoque.representante.record;

import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.estoque.representante.model.RepresentanteModel;

	public record DetalheRepresentanteRecord(	
			String nome,
			String celular,
			Boolean ativo,
			DetalheFornecedorRecord fornecedor
			) {
		
	public DetalheRepresentanteRecord(RepresentanteModel dados) {
		
			this(	
					dados.getNome(),
					dados.getCelular(),
					dados.getAtivo(),
					new DetalheFornecedorRecord(dados.getFornecedor())
			);
				
	}

}