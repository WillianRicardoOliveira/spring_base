package com.empresa.erp.domain.estoque.movimentacao.model;

import java.time.LocalDateTime;

import com.empresa.erp.domain.estoque.compra.model.CompraModel;
import com.empresa.erp.domain.estoque.movimentacao.record.MovimentacaoRecord;
import com.empresa.erp.domain.estoque.movimentacao.tipoMovimentacao.model.TipoMovimentacaoModel;
import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;
import com.empresa.erp.util.Formatacao;

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
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_tipo_movimentacao")
	private TipoMovimentacaoModel tipoMovimentacao;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_compra")
	private CompraModel compra;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_produto")
	private ProdutoModel produto;
	private Integer quantidade;
	private Integer total;
	private LocalDateTime data;
	private Boolean ativo;
	
	public MovimentacaoModel(MovimentacaoRecord dados, CompraModel compra, ProdutoModel produto, Integer total, TipoMovimentacaoModel tipoMovimentacao) {	
		this.tipoMovimentacao = tipoMovimentacao;
		this.compra = compra;
		this.produto = produto;
		this.quantidade = dados.quantidade();
		this.total = total;	
		this.data = LocalDateTime.now();
		this.ativo = true;
	}
	
	public String dataFormatada() {
		return Formatacao.formataData(getData().toString());
	}
	
}
