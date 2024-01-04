package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model;

import home.office.spring.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.AtualizaSubCategoriaContaRecord;
import home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record.SubCategoriaContaRecord;
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

@Table(name = "sub_categoria_conta")
@Entity(name = "SubCategoriaContaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SubCategoriaContaModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_categoria_conta")
	private CategoriaContaModel categoriaConta;
	private Boolean ativo;
	
	public SubCategoriaContaModel(SubCategoriaContaRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaSubCategoriaContaRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
