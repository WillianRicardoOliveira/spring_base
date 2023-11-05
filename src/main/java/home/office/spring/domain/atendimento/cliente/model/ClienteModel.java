package home.office.spring.domain.atendimento.cliente.model;

import home.office.spring.domain.atendimento.cliente.record.AtualizaClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.ClienteRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "cliente")
@Entity(name = "ClienteModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ClienteModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String telefone;	
	private Boolean ativo;
	
	public ClienteModel(ClienteRecord dados) {	
		this.nome = dados.nome();
		this.telefone = dados.telefone();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaClienteRecord dados) { 		
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
		if(dados.telefone() != null) {
			this.telefone = dados.telefone();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
