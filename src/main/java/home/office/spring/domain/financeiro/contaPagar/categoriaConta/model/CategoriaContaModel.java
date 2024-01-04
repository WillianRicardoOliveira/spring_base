package home.office.spring.domain.financeiro.contaPagar.categoriaConta.model;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.record.AtualizaCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.record.CategoriaContaRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "categoria_conta")
@Entity(name = "CategoriaContaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CategoriaContaModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
	
	public CategoriaContaModel(CategoriaContaRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaCategoriaContaRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
