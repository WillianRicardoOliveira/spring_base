package home.office.spring.domain.fiscal.fornecedor.model;

import home.office.spring.domain.fiscal.endereco.model.EnderecoModel;
import home.office.spring.domain.fiscal.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.FornecedorRecord;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Boolean tipo;
	
	private String numeroDocumento;
	
	private String razaoSocial;
	
	private String nomeFantasia;
	
	private String inscricaoEstadual;
	
	private String inscricaoMunicipal;
	
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_regime_tributacao_federal")
	private RegimeTributacaoFederalModel regimeTributacaoFederal;
	
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_setor_atividade")
	private SetorAtividadeModel setorAtividade;
	
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_endereco")
	private EnderecoModel endereco;
	
	@NotNull
	private Boolean ativo;
	
	public FornecedorModel(FornecedorRecord dados, RegimeTributacaoFederalModel regimeTributacaoFederal, SetorAtividadeModel setorAtividade, EnderecoModel endereco) {	
		
		this.tipo = dados.tipo();	
		
		this.numeroDocumento = dados.numeroDocumento();
		
		this.razaoSocial = dados.razaoSocial();
		
		this.nomeFantasia = dados.nomeFantasia();
		
		this.inscricaoEstadual = dados.inscricaoEstadual(); 
		
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		
		this.regimeTributacaoFederal = regimeTributacaoFederal;
		
		this.setorAtividade = setorAtividade;
		
		this.endereco = endereco;
		
		this.ativo = true;			
	}
	
	public void atualizar(AtualizaFornecedorRecord dados, RegimeTributacaoFederalModel regimeTributacaoFederal, SetorAtividadeModel setorAtividade) { 		
		
		if(dados.tipo() != null) {
		
			this.tipo = dados.tipo();
		
		}
		
		if(dados.numeroDocumento() != null) {
		
			this.numeroDocumento = dados.numeroDocumento();
		
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
		
		if(dados.endereco() != null) {
			
			this.endereco.atualizar(dados.endereco());
			
		}
		
	}
	
	public void ativo(Boolean ativo) {
		
		this.ativo = ativo;
		
	}
	
}
