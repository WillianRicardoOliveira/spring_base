package home.office.spring.domain.estoque.movimentacao.record;

import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;

public record ListaMovimentacaoRecord(				
		Long id,
		String tipoMovimentacao,
		String produto,
		Integer quantidade,
		Integer total,
		String data
) {
	
	public ListaMovimentacaoRecord(MovimentacaoModel dados) {
		this(
				dados.getId(),
				dados.getTipoMovimentacao().getNome(),				
				dados.getProduto().getNome(),
				dados.getQuantidade(),
				dados.getTotal(),
				dados.dataFormatada()
		);
	}	

}
