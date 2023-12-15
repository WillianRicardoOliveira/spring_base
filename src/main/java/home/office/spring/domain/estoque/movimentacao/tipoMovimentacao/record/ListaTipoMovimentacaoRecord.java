package home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record;

import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.model.TipoMovimentacaoModel;

public record ListaTipoMovimentacaoRecord(				
		Long id,
		String nome
) {
	
	public ListaTipoMovimentacaoRecord(TipoMovimentacaoModel dados) {
		this(
				dados.getId(),
				dados.getNome()
		);
	}	
	
}
