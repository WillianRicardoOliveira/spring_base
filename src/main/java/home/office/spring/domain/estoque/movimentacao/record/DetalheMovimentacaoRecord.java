package home.office.spring.domain.estoque.movimentacao.record;

import java.time.LocalDateTime;

import home.office.spring.domain.atendimento.cliente.record.DetalheClienteRecord;
import home.office.spring.domain.estoque.compra.record.DetalheCompraRecord;
import home.office.spring.domain.estoque.movimentacao.constante.TipoMovimentacao;
import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;

	public record DetalheMovimentacaoRecord(	
			TipoMovimentacao tipoMovimentacao,
			DetalheCompraRecord compra,
			DetalheClienteRecord cliente,
			DetalheProdutoRecord produto,
			Integer quantidade,
			Integer total,
			LocalDateTime data,
			Boolean ativo		
			) {
		
	public DetalheMovimentacaoRecord(MovimentacaoModel dados) {
		
			this(	
					dados.getTipoMovimentacao(),
					
					dados.getCompra() != null ? new DetalheCompraRecord(dados.getCompra()) : new DetalheCompraRecord(),
					
					dados.getCliente() != null ? new DetalheClienteRecord(dados.getCliente()) : new DetalheClienteRecord(),
										
					new DetalheProdutoRecord(dados.getProduto()),
					dados.getQuantidade(),
					dados.getTotal(),
					dados.getData(),
					dados.getAtivo()
			);
				
	}

}