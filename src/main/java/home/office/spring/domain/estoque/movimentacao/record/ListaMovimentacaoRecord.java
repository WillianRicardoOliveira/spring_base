package home.office.spring.domain.estoque.movimentacao.record;

import java.time.LocalDateTime;

import home.office.spring.domain.estoque.movimentacao.constante.TipoMovimentacao;
import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;

public record ListaMovimentacaoRecord(				
		Long id,
		TipoMovimentacao tipoMovimentacao,
		String produto,
		Integer quantidade,
		Integer total,
		String data
) {
	
	public ListaMovimentacaoRecord(MovimentacaoModel dados) {
		this(
				dados.getId(),
				dados.getTipoMovimentacao(),				
				dados.getProduto().getNome(),
				dados.getQuantidade(),
				dados.getTotal(),
				dados.dataFormatada()
		);
	}	

}
