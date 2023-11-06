package home.office.spring.domain.estoque.compra.model;

import home.office.spring.domain.estoque.compra.constante.Status;
import home.office.spring.domain.estoque.compra.record.AtualizaCompraRecord;
import home.office.spring.domain.estoque.compra.record.CompraRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "compra")
@Entity(name = "CompraModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CompraModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String descricao;
	@Enumerated(EnumType.STRING)
	private Status status;
	private String data;	
	private Boolean ativo;
	
	public CompraModel(CompraRecord dados) {	
		this.descricao = dados.descricao();
		this.status = dados.status();
		this.data = dados.data();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaCompraRecord dados) { 		
		if(dados.descricao() != null) {
			this.descricao = dados.descricao();
		}		
		if(dados.status() != null) {
			this.status = dados.status();
		}
		if(dados.data() != null) {
			this.data = dados.data();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
