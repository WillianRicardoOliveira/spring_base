package com.empresa.erp.domain.estoque.compraItem.model;
/*
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.empresa.erp.domain.estoque.compra.model.CompraModel;
import com.empresa.erp.domain.estoque.compraItem.record.AtualizaCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.CompraItemRecord;
import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;
import com.empresa.erp.domain.fiscal.fornecedor.model.FornecedorModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "compraItem")
@Entity(name = "CompraItemModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CompraItemModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_compra")
	private CompraModel compra;	
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_fornecedor")
	private FornecedorModel fornecedor;	
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_produto")
	private ProdutoModel produto;	
	private Integer quantidade;
	private BigDecimal valor;
	private BigDecimal total;
	private Integer controle;
	private Boolean ativo;
	
	public CompraItemModel(CompraItemRecord dados, CompraModel compra, FornecedorModel fornecedor, ProdutoModel produto, BigDecimal total, Integer controle) {	
		this.compra = compra;
		this.fornecedor = fornecedor;
		this.produto = produto;
		this.quantidade = dados.quantidade();
		this.valor = dados.valor().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
		this.total = total;
		this.controle = controle;
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaCompraItemRecord dados, FornecedorModel fornecedor, ProdutoModel produto, BigDecimal total, Integer controle) { 		
		if(fornecedor != null) {
			this.fornecedor = fornecedor;
		}		
		if(produto != null) {
			this.produto = produto;
		}
		if(dados.quantidade() != null) {
			this.quantidade = dados.quantidade();
		}
		if(dados.valor() != null) {
			this.valor = dados.valor().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
		}
		if(total != null) {
			this.total = total;
		}
		if(controle != null) {
			this.controle = controle;
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
	
}
*/