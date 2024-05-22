package home.office.spring.domain.financeiro.contaPagar.banco.conta.model;

import home.office.spring.domain.financeiro.contaPagar.banco.conta.record.AtualizaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.conta.record.ContaRecord;
import home.office.spring.domain.financeiro.contaPagar.banco.model.BancoModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "banco")
@Entity(name = "ContaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ContaModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_banco")
	private BancoModel banco;	
	private String agencia;
	private String conta;
	private String digito;
	private String pix;
	private Boolean ativo;
	
	public ContaModel(ContaRecord dados, BancoModel banco) {	
		this.banco = banco;
		this.agencia = dados.agencia();
		this.conta = dados.conta();
		this.digito = dados.digito();
		this.pix = dados.pix();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaContaRecord dados, BancoModel banco) {
		if(banco != null) {
			this.banco = banco;
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
