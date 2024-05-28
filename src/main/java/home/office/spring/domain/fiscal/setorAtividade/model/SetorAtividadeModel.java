package home.office.spring.domain.fiscal.setorAtividade.model;

import home.office.spring.domain.fiscal.setorAtividade.record.AtualizaSetorAtividadeRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.SetorAtividadeRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "setor_atividade")
@Entity(name = "SetorAtividadeModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SetorAtividadeModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
	
	public SetorAtividadeModel(SetorAtividadeRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaSetorAtividadeRecord dados) { 		
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
	}
	
	public void inativar() {
		this.ativo = false;
	}
	
}
