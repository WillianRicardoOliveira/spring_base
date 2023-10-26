package home.office.spring.domain.endereco.model;

import home.office.spring.domain.endereco.record.EnderecoRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
	private String cep = "";
	private String logradouro = "";
	private String complemento = "";
    private String bairro = "";    
    private String localidade = "";
    private String uf = "";
    private String numero = "";    
    
    public EnderecoModel(EnderecoRecord dados) {
    	this.cep = dados.cep();
    	this.logradouro = dados.logradouro();
    	this.complemento = dados.complemento();
    	this.bairro = dados.bairro();
    	this.localidade = dados.localidade();
    	this.uf = dados.uf();
    	this.numero = dados.numero();
    }
    
    public void atualizar(EnderecoRecord dados) {
    	if(dados.cep() != null) {
    		this.cep = dados.cep();
    	}
    	if(dados.logradouro() != null) {
    		this.logradouro = dados.logradouro();
    	}
    	if(dados.complemento() != null) {
    		this.complemento = dados.complemento();
    	}
    	if(dados.bairro() != null) {
    		this.bairro = dados.bairro();
    	}
    	
    	if(dados.localidade() != null) {
    		this.localidade = dados.localidade();
    	}
    	if(dados.uf() != null) {
    		this.uf = dados.uf();
    	}
    	if(dados.numero() != null) {
    		this.numero = dados.numero();
    	}
    	
    }
    
}
