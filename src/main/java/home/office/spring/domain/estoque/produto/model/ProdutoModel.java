package home.office.spring.domain.estoque.produto.model;

import home.office.spring.domain.estoque.produto.record.AtualizaProdutoRecord;
import home.office.spring.domain.estoque.produto.record.ProdutoRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "produto")
@Entity(name = "ProdutoModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProdutoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String descricao;
	private Integer quantidade;
	private Integer minimo;
	private Integer maximo;
	private Boolean ativo;
	
	public ProdutoModel(ProdutoRecord dados) {
		this.nome = dados.nome();
		this.descricao = dados.descricao();
		this.quantidade = dados.quantidade();
		this.minimo = dados.minimo();
		this.maximo = dados.maximo();
		this.ativo = true;
	}
	
	public void atualizar(AtualizaProdutoRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
		if(dados.descricao() != null) {
			this.descricao = dados.descricao();
		}
		if(dados.quantidade() != null) {
			this.quantidade = dados.quantidade();
		}
		if(dados.minimo() != null) {
			this.minimo = dados.minimo();
		}
		if(dados.maximo() != null) {
			this.maximo = dados.maximo();
		}
		
	}

	public void inativar() {
		this.ativo = false;
	}
	
}
