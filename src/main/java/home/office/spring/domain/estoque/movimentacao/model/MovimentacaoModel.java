package home.office.spring.domain.estoque.movimentacao.model;

import java.time.LocalDateTime;

import home.office.spring.domain.atendimento.cliente.model.ClienteModel;
import home.office.spring.domain.estoque.compra.model.CompraModel;
import home.office.spring.domain.estoque.movimentacao.constante.TipoMovimentacao;
import home.office.spring.domain.estoque.movimentacao.record.MovimentacaoRecord;
import home.office.spring.domain.estoque.produto.model.ProdutoModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "movimentacao")
@Entity(name = "MovimentacaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class MovimentacaoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private TipoMovimentacao tipoMovimentacao;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_compra")
	private CompraModel compra;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_cliente")
	private ClienteModel cliente;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_produto")
	private ProdutoModel produto;
	private Integer quantidade;
	private Integer total;
	private LocalDateTime data;
	private Boolean ativo;
	
	public MovimentacaoModel(MovimentacaoRecord dados, CompraModel compra, ClienteModel cliente, ProdutoModel produto) {	
		this.tipoMovimentacao = dados.tipoMovimentacao();
		this.compra = compra;
		this.cliente = cliente;
		this.produto = produto;
		this.quantidade = dados.quantidade();
		this.total = dados.total();	
		this.data = LocalDateTime.now();
		this.ativo = true;
	}
	
}
