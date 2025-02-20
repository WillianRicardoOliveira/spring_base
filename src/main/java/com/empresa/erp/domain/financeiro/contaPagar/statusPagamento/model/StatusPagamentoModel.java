package com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.model;

import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.AtualizaStatusPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.StatusPagamentoRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "status_pagamento")
@Entity(name = "StatusPagamentoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class StatusPagamentoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
		
	public StatusPagamentoModel(StatusPagamentoRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaStatusPagamentoRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
