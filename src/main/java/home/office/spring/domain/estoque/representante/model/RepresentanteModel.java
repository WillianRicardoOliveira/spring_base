package home.office.spring.domain.estoque.representante.model;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.representante.record.AtualizaRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.RepresentanteRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "representante")
@Entity(name = "RepresentanteModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RepresentanteModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String celular;
	@OneToMany(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_fornecedor")
	private FornecedorModel fornecedor;
	private Boolean ativo;
	
	public RepresentanteModel(RepresentanteRecord dados, FornecedorModel fornecedor) {
		this.nome = dados.nome();
		this.celular = dados.celular();
		this.fornecedor = fornecedor;
		this.ativo = true;
	}
	
	public void atualizar(AtualizaRepresentanteRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
		if(dados.celular() != null) {
			this.celular = dados.celular();
		}
	}

	public void inativar() {
		this.ativo = false;
	}
	
}
