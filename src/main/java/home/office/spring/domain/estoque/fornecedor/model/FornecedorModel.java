package home.office.spring.domain.estoque.fornecedor.model;

import home.office.spring.domain.estoque.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.FornecedorRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "fornecedor")
@Entity(name = "FornecedorModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class FornecedorModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String cnpj;
	private String nome;
	private Boolean ativo;
	
	public FornecedorModel(FornecedorRecord dados) {	
		this.cnpj = dados.cnpj();
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaFornecedorRecord dados) { 		
		if(dados.cnpj() != null) {
			this.cnpj = dados.cnpj();
		}
		
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}	
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
