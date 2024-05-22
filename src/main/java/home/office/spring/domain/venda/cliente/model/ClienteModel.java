package home.office.spring.domain.venda.cliente.model;

import home.office.spring.domain.endereco.model.EnderecoModel;
import home.office.spring.domain.venda.cliente.record.AtualizaClienteRecord;
import home.office.spring.domain.venda.cliente.record.ClienteRecord;
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
	private String razaoSocial;
	private String cnpj;
	private String inscricaoEstadual;
	private EnderecoModel endereco;
	private Boolean ativo;
	
	public ClienteModel(ClienteRecord dados, EnderecoModel enderecoModel) {	
		this.razaoSocial = dados.razaoSocial();
		this.cnpj = dados.cnpj();		
		this.inscricaoEstadual = dados.inscricaoEstadual();
		this.endereco = enderecoModel;
		this.ativo = true;
	}
	
	public void atualizar(AtualizaClienteRecord dados, EnderecoModel enderecoModel) { 		
		if(dados.razaoSocial() != null) {
			this.razaoSocial = dados.razaoSocial();
		}
		if(dados.cnpj() != null) {
			this.cnpj = dados.cnpj();
		}
		if(dados.inscricaoEstadual() != null) {
			this.inscricaoEstadual = dados.inscricaoEstadual();
		}
		if(enderecoModel != null) {
			this.endereco = enderecoModel;
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}