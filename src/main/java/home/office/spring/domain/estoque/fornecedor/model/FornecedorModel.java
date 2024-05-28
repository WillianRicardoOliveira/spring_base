package home.office.spring.domain.estoque.fornecedor.model;

import home.office.spring.domain.estoque.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;
import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
	private String razaoSocial;	
	private String nomeFantasia;
	private String inscricaoEstadual;
	private String inscricaoMunicipal;
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_regime_tributação_federal")
	private RegimeTributacaoFederalModel regimeTributacaoFederal;
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_setor_atividade")
	private SetorAtividadeModel setorAtividade;
	private Boolean ativo;
	
	public FornecedorModel(FornecedorRecord dados, RegimeTributacaoFederalModel regimeTributacaoFederal, SetorAtividadeModel setorAtividade) {	
		this.cnpj = dados.cnpj();
		this.razaoSocial = dados.razaoSocial();
		this.nomeFantasia = dados.nomeFantasia();
		this.inscricaoEstadual = dados.inscricaoEstadual(); 
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		this.regimeTributacaoFederal = regimeTributacaoFederal;
		this.setorAtividade = setorAtividade;
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaFornecedorRecord dados, RegimeTributacaoFederalModel regimeTributacaoFederal, SetorAtividadeModel setorAtividade) { 		
		if(dados.cnpj() != null) {
			this.cnpj = dados.cnpj();
		}		
		if(dados.razaoSocial() != null) {
			this.razaoSocial = dados.razaoSocial();
		}
		if(dados.nomeFantasia() != null) {
			this.nomeFantasia = dados.nomeFantasia();
		}
		if(dados.inscricaoEstadual() != null) {
			this.inscricaoEstadual = dados.inscricaoEstadual();
		}
		if(dados.inscricaoMunicipal() != null) {
			this.inscricaoMunicipal = dados.inscricaoMunicipal();
		}
		if(regimeTributacaoFederal != null) {
			this.regimeTributacaoFederal = regimeTributacaoFederal;
		}
		if(setorAtividade != null) {
			this.setorAtividade = setorAtividade;
		}	
	}
	
	public void inativar() {
		this.ativo = false;
	}
	
	/*	
	public String cnpjFormatado() {
		return Formatacao.formataCnpj(getCnpj());
	}
	
	public String telefoneFormatado() {
		return Formatacao.formataTelefone(getTelefone());
	}
	*/
}
