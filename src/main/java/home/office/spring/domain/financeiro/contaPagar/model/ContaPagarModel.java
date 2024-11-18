package home.office.spring.domain.financeiro.contaPagar.model;
/*
import java.math.BigDecimal;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;
import home.office.spring.domain.financeiro.contaPagar.record.AtualizaContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.record.ContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;
import home.office.spring.domain.fiscal.fornecedor.model.FornecedorModel;
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

@Table(name = "conta_pagar")
@Entity(name = "ContaPagarModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ContaPagarModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_fornecedor")
	private FornecedorModel fornecedor;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_sub_categoria")
	private SubCategoriaContaModel subCategoriaConta;
	private String descricao;
	private BigDecimal valor;
	private Integer parcelas;	
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_status_pagamento")
	private StatusPagamentoModel statusPagamento;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_forma_pagamento")
	private FormaPagamentoModel formaPagamento;	
	private Boolean ativo;
		
	public ContaPagarModel(ContaPagarRecord dados, FornecedorModel fornecedor, SubCategoriaContaModel subCategoriaConta, StatusPagamentoModel statusPagamento, FormaPagamentoModel formaPagamento) {	
		this.fornecedor = fornecedor;
		this.subCategoriaConta = subCategoriaConta;
		this.descricao = dados.descricao();
		this.valor = dados.valor();
		this.parcelas = dados.parcelas();
		this.statusPagamento = statusPagamento;
		this.formaPagamento = formaPagamento;
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaContaPagarRecord dados, FornecedorModel fornecedor, SubCategoriaContaModel subCategoriaConta, StatusPagamentoModel statusPagamento, FormaPagamentoModel formaPagamento) {
		if(fornecedor != null) {
			this.fornecedor = fornecedor;
		}
		if(subCategoriaConta != null) {
			this.subCategoriaConta = subCategoriaConta;
		}
		if(dados.descricao() != null) {
			this.descricao = dados.descricao();
		}
		if(dados.valor() != null) {
			this.valor = dados.valor();
		}
		if(dados.parcelas() != null) {
			this.parcelas = dados.parcelas();
		}
		if(statusPagamento != null) {
			this.statusPagamento = statusPagamento;
		}
		if(formaPagamento != null) {
			this.formaPagamento = formaPagamento;
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
*/