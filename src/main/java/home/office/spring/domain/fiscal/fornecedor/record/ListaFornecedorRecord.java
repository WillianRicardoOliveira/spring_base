package home.office.spring.domain.fiscal.fornecedor.record;

import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;

public record ListaFornecedorRecord(
	Long id,
	Boolean tipo,
	String numeroDocumento,
	String razaoSocial,
	String nomeFantasia,
	String inscricaoEstadual,
	String inscricaoMunicipal,
	String regimeTributacaoFederal,
	String setorAtividade	
) {
	
	public ListaFornecedorRecord(FornecedorModel dados) {
		this(
			dados.getId(),
			dados.getTipo(),
			dados.getNumeroDocumento(),
			dados.getRazaoSocial(),
			dados.getNomeFantasia(),
			dados.getInscricaoEstadual(),
			dados.getInscricaoMunicipal(),
			dados.getRegimeTributacaoFederal().getNome(),
			dados.getSetorAtividade().getNome()	
		);
	}	

}
