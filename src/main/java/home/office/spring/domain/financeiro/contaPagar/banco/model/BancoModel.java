package home.office.spring.domain.financeiro.contaPagar.banco.model;

import home.office.spring.domain.financeiro.contaPagar.banco.record.AtualizaBancoRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.record.BancoRecord;
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
	private String agencia;
	private String conta;
	private String digito;
	private String pix;
	private Boolean ativo;
	
	public BancoModel(BancoRecord dados) {	
		this.nome = dados.nome();
		this.nome = dados.agencia();
		this.nome = dados.conta();
		this.nome = dados.digito();
		this.nome = dados.pix();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaBancoRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
		if(dados.agencia() != null) {
			this.agencia = dados.agencia();
		}
		if(dados.conta() != null) {
			this.conta = dados.conta();
		}
		if(dados.digito() != null) {
			this.digito = dados.digito();
		}
		if(dados.pix() != null) {
			this.pix = dados.pix();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
