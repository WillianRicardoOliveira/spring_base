package home.office.spring.domain.financeiro.contaPagar.model;

import home.office.spring.domain.financeiro.contaPagar.record.AtualizaContaPagarRecord;
import home.office.spring.domain.financeiro.contaPagar.record.ContaPagarRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "conta_pagar")
@Entity(name = "ContaPagarModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ContaPagarModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
	
	public ContaPagarModel(ContaPagarRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaContaPagarRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
