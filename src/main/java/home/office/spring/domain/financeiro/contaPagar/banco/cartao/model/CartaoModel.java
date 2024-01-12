package home.office.spring.domain.financeiro.contaPagar.banco.cartao.model;

import home.office.spring.domain.financeiro.contaPagar.banco.cartao.record.AtualizaCartaoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.cartao.record.CartaoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.conta.model.ContaModel;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;
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

@Table(name = "cartao")
@Entity(name = "CartaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CartaoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_conta")
	private ContaModel conta;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_forma_pagamento")
	private FormaPagamentoModel formaPagamento;
	private String numeroCartao;
	private String validadeMes;
	private String validadeAno;
	private Boolean ativo;
	
	public CartaoModel(CartaoRecord dados, ContaModel conta, FormaPagamentoModel formaPagamento) {	
		this.conta = conta;		
		this.formaPagamento = formaPagamento;		
		this.numeroCartao = dados.numeroCartao();
		this.validadeMes = dados.validadeMes();
		this.validadeAno = dados.validadeAno();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaCartaoRecord dados, ContaModel conta, FormaPagamentoModel formaPagamento) {
		if(conta != null) {
			this.conta = conta;
		}
		if(formaPagamento != null) {
			this.formaPagamento = formaPagamento;
		}
		if(dados.numeroCartao() != null) {
			this.numeroCartao = dados.numeroCartao();
		}
		if(dados.validadeMes() != null) {
			this.validadeMes = dados.validadeMes();
		}
		if(dados.validadeAno() != null) {
			this.validadeAno = dados.validadeAno();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
