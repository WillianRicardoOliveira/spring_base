package com.empresa.erp.domain.financeiro.contaPagar.banco.model;

import com.empresa.erp.domain.financeiro.contaPagar.banco.record.AtualizaBancoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.banco.record.BancoRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "banco")
@Entity(name = "BancoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BancoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
	
	public BancoModel(BancoRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaBancoRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
