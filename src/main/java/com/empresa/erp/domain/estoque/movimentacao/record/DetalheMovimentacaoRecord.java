package com.empresa.erp.domain.estoque.movimentacao.record;

import java.time.LocalDateTime;

import com.empresa.erp.domain.estoque.compra.record.DetalheCompraRecord;
import com.empresa.erp.domain.estoque.movimentacao.model.MovimentacaoModel;
import com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.record.DetalheTipoMovimentacaoRecord;
import com.empresa.erp.domain.estoque.produto.record.DetalheProdutoRecord;

	public record DetalheMovimentacaoRecord(
			Long id,
			DetalheTipoMovimentacaoRecord tipoMovimentacao,
			DetalheCompraRecord compra,
			DetalheProdutoRecord produto,
			Integer quantidade,
			Integer total,
			LocalDateTime data
			) {
		
	public DetalheMovimentacaoRecord(MovimentacaoModel dados) {
		
			this(	
					dados.getId(),
					new DetalheTipoMovimentacaoRecord(dados.getTipoMovimentacao()),
					dados.getCompra() != null ? new DetalheCompraRecord(dados.getCompra()) : new DetalheCompraRecord(),
					new DetalheProdutoRecord(dados.getProduto()),
					dados.getQuantidade(),
					dados.getTotal(),
					dados.getData()
			);
				
	}

}