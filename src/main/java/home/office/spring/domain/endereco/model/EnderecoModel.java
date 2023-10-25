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
	private String logradouro;
    private String bairro;
    private String cep;
    @OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_estado")
    private EstadoModel estado;
    private String cidade;
    private String numero;
    private String complemento;
    
    public EnderecoModel(EnderecoRecord dados, EstadoModel estado) {
    	this.logradouro = dados.logradouro();
    	this.bairro = dados.bairro();
    	this.cep = dados.cep();
    	this.estado = estado;
    	this.cidade = dados.cidade();
    	this.numero = dados.numero();
    	this.complemento = dados.complemento();
    }
    
    public void atualizar(EnderecoRecord dados) {
    	if(dados.logradouro() != null) {
    		this.logradouro = dados.logradouro();
    	}
    	if(dados.bairro() != null) {
    		this.bairro = dados.bairro();
    	}
    	if(dados.cep() != null) {
    		this.cep = dados.cep();
    	}
    	if(dados.estado() != null) {
    		this.estado.atualizar(dados.estado());
    	}
    	if(dados.cidade() != null) {
    		this.cidade = dados.cidade();
    	}
    	if(dados.numero() != null) {
    		this.numero = dados.numero();
    	}
    	if(dados.complemento() != null) {
    		this.complemento = dados.complemento();
    	}
    }
    
}
