package com.empresa.erp.domain.old;

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
