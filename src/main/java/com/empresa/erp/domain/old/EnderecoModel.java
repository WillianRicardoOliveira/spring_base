package com.empresa.erp.domain.old;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "endereco")
@Entity(name = "EnderecoModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class EnderecoModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String cep;
	private String localidade;
	private String uf;
	private String bairro;
	private String logradouro;
	private String numero;
	private String complemento;
	@Enumerated(EnumType.ORDINAL)
	private StatusEnum status;	
    public EnderecoModel(EnderecoRecord dados) {
    	this.cep = dados.cep();
    	this.localidade = dados.localidade();    	
    	this.uf = dados.uf();    	
    	this.bairro = dados.bairro();    	
    	this.logradouro = dados.logradouro();    	
    	this.numero = dados.numero();    	
    	this.complemento = dados.complemento();
    	this.status = StatusEnum.ATIVO;
    }    
    public void atualizar(EnderecoRecord dados) {
    	this.cep = dados.cep();
    	this.localidade = dados.localidade();    	
    	this.uf = dados.uf();
    	this.bairro = dados.bairro();    
    	this.logradouro = dados.logradouro();
    	this.numero = dados.numero();
    	this.complemento = dados.complemento();
    }
}