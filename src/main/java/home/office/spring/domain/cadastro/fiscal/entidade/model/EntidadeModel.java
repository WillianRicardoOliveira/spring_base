package home.office.spring.domain.cadastro.fiscal.entidade.model;

import java.util.List;
import java.util.stream.Collectors;

import home.office.spring.domain.base.model.BaseModel;
import home.office.spring.domain.cadastro.fiscal.entidade.record.AtualizaEntidadeRecord;
import home.office.spring.domain.cadastro.fiscal.entidade.record.EntidadeRecord;
import home.office.spring.domain.fiscal.endereco.model.EnderecoModel;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;
import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "entidade")
@Entity(name = "EntidadeModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class EntidadeModel extends BaseModel {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nomeCompleto;
	private String nomeFantasia;	
	@OneToMany(mappedBy = "entidade", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EntidadeTipoModel> entidadeTipos;
	private String numeroDocumento;	
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
		
	public EntidadeModel(
			EntidadeRecord dados,
			RegimeTributacaoFederalModel regimeTributacaoFederal,
			SetorAtividadeModel setorAtividade,
			EnderecoModel endereco
	) {
		super();
		this.nomeCompleto = dados.nomeCompleto();
		//this.nomeFantasia = dados.nomeFantasia();		
		//this.entidadeTipos = dados.entidadeTipos().stream().map(tipo -> new EntidadeTipoModel(this, tipo)).collect(Collectors.toList());		
		this.numeroDocumento = dados.numeroDocumento();
		this.inscricaoEstadual = dados.inscricaoEstadual();		
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		this.regimeTributacaoFederal = regimeTributacaoFederal;		
		this.setorAtividade = setorAtividade;
		this.endereco = endereco;
	}
	
	public void atualizar(
			AtualizaEntidadeRecord dados,
			RegimeTributacaoFederalModel regimeTributacaoFederal,
			SetorAtividadeModel setorAtividade
	) {
		if(dados.nomeCompleto() != null) {
			this.nomeCompleto = dados.nomeCompleto();
		}
		//if(dados.nomeFantasia() != null) {
		//	this.nomeFantasia = dados.nomeFantasia();
		//}
		//if (dados.entidadeTipos() != null) {
		//    this.entidadeTipos.clear();
		//    this.entidadeTipos.addAll(dados.entidadeTipos().stream().map(tipo -> new EntidadeTipoModel(this, tipo)).collect(Collectors.toList()));
		//}
		if(dados.numeroDocumento() != null) {		
			this.numeroDocumento = dados.numeroDocumento();		
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
	
}
