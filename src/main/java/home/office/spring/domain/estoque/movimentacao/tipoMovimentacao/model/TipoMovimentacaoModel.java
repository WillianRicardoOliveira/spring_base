package home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.model;

import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.AtualizaTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.TipoMovimentacaoRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "tipoMovimentacao")
@Entity(name = "TipoMovimentacaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TipoMovimentacaoModel {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Boolean ativo;
	
	public TipoMovimentacaoModel(TipoMovimentacaoRecord dados) {	
		this.nome = dados.nome();
		this.ativo = true;
	}
	
	public void atualizar(AtualizaTipoMovimentacaoRecord dados) { 		
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}				
	}
	
	public void inativar() {
		this.ativo = false;
	}
	
}
