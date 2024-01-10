package home.office.spring.domain.financeiro.contaPagar.formaPagamento.model;

import home.office.spring.domain.financeiro.contaPagar.formaPagamento.record.AtualizaFormaPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.formaPagamento.record.FormaPagamentoRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "forma_pagamento")
@Entity(name = "FormaPagamentoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FormaPagamentoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
		
	public FormaPagamentoModel(FormaPagamentoRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaFormaPagamentoRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
