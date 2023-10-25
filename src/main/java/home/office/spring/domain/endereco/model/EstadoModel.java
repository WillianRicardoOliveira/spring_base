package home.office.spring.domain.endereco.model;

import home.office.spring.domain.endereco.record.EstadoRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "estado")
@Entity(name = "EstadoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EstadoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
    private String sigla;
    
    public EstadoModel(EstadoRecord dados) {
    	this.nome = dados.nome();
    	this.sigla = dados.sigla();
    }
    
    public void atualizar(EstadoRecord dados) {
    	if(dados.nome() != null) {
    		this.nome = dados.nome();
    	}
    	if(dados.sigla() != null) {
    		this.sigla = dados.sigla();
    	}
    }
	
}
