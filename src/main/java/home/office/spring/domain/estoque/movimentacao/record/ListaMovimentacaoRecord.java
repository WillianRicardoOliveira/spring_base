package home.office.spring.domain.estoque.movimentacao.record;

import java.time.LocalDateTime;

import home.office.spring.domain.atendimento.cliente.record.ListaClienteRecord;
import home.office.spring.domain.estoque.compra.record.ListaCompraRecord;
import home.office.spring.domain.estoque.movimentacao.constante.TipoMovimentacao;
import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;
import home.office.spring.domain.estoque.produto.record.ListaProdutoRecord;

public record ListaMovimentacaoRecord(				
		Long id,
		TipoMovimentacao tipoMovimentacao,
		ListaCompraRecord compra,
		ListaClienteRecord cliente,
		ListaProdutoRecord produto,
		Integer quantidade,
		Integer total,
		LocalDateTime data,
		Boolean ativo
) {
	
	public ListaMovimentacaoRecord(MovimentacaoModel dados) {
		this(
				dados.getId(),
				dados.getTipoMovimentacao(),				
				new ListaCompraRecord(dados.getCompra()),
				new ListaClienteRecord(dados.getCliente()),
				new ListaProdutoRecord(dados.getProduto()),				
				dados.getQuantidade(),
				dados.getTotal(),
				dados.getData(),
				dados.getAtivo()
		);
	}	

}
