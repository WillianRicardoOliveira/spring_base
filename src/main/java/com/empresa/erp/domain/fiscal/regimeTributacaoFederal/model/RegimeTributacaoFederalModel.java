package com.empresa.erp.domain.fiscal.regimeTributacaoFederal.model;

import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.AtualizaRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.RegimeTributacaoFederalRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "regime_tributacao_federal")
@Entity(name = "RegimeTributacaoFederalModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RegimeTributacaoFederalModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
	
	public RegimeTributacaoFederalModel(RegimeTributacaoFederalRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaRegimeTributacaoFederalRecord dados) { 		
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
	
}
