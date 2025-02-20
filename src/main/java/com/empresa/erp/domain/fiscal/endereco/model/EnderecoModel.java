package com.empresa.erp.domain.fiscal.endereco.model;

import com.empresa.erp.domain.fiscal.endereco.record.EnderecoRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "endereco")
@Entity(name = "EnderecoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EnderecoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String cep;
	private String localidade;
	private String uf;
	private String bairro;
	private String logradouro;
	private String numero = "";
	private String complemento;
	private Boolean ativo;
	
    public EnderecoModel(EnderecoRecord dados) {
    	this.cep = dados.cep();
    	this.localidade = dados.localidade();
    	this.uf = dados.uf();
    	this.bairro = dados.bairro();
    	this.logradouro = dados.logradouro();
    	this.numero = dados.numero();
    	this.complemento = dados.complemento();
    	this.ativo = true;
    }
    
    public void atualizar(EnderecoRecord dados) {
    	if(dados.cep() != null) {
    		this.cep = dados.cep();
    	}
    	if(dados.localidade() != null) {
    		this.localidade = dados.localidade();
    	}
    	if(dados.uf() != null) {
    		this.uf = dados.uf();
    	}
    	if(dados.bairro() != null) {
    		this.bairro = dados.bairro();
    	}
    	if(dados.logradouro() != null) {
    		this.logradouro = dados.logradouro();
    	}
    	if(dados.numero() != null) {
    		this.numero = dados.numero();
    	}
    	if(dados.complemento() != null) {
    		this.complemento = dados.complemento();
    	}
    }
    
    public void ativo(Boolean ativo) {
		
		this.ativo = ativo;
		
	}
    
}
