package home.office.spring.domain.cadastro.fiscal.entidade.record;

import home.office.spring.domain.cadastro.fiscal.entidade.model.EntidadeModel;

public record ListaEntidadeRecord(
		Long id,
		String nomeCompleto,
		String nomeFantasia,
		String numeroDocumento,
		String inscricaoEstadual,
		String inscricaoMunicipal,
		String regimeTributacaoFederal,
		String setorAtividade,
		Boolean ativo
) {
	
	public ListaEntidadeRecord(EntidadeModel dados) {
		this(
			dados.getId(),
			dados.getNomeCompleto(),
			dados.getNomeFantasia(),			
			dados.getNumeroDocumento(),			
			dados.getInscricaoEstadual(),
			dados.getInscricaoMunicipal(),
			dados.getRegimeTributacaoFederal().getNome(),
			dados.getSetorAtividade().getNome(),
			dados.getAtivo()
		);
	}	

}
