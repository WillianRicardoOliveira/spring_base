package com.empresa.erp.fiscal.entidade.model;

import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;
import com.empresa.erp.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import com.empresa.erp.fiscal.entidade.record.AtualizaEntidadeRecord;
import com.empresa.erp.fiscal.entidade.record.EntidadeRecord;

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
import lombok.Setter;

@Table(name = "entidade")
@Entity(name = "EntidadeModel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class EntidadeModel {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nomeCompleto;
	private String numeroDocumento;
	private String inscricaoEstadual;	
	private String inscricaoMunicipal;
	private Boolean cliente;	
	private Boolean fornecedor;	
	private Boolean parceiro;
	private Boolean transportador;
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_regime_tributacao_federal")
	private RegimeTributacaoFederalModel regimeTributacaoFederal;
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_setor_atividade")
	private SetorAtividadeModel setorAtividade;
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_endereco")
	private EnderecoModel endereco;
	private String contatoPrincipal;
	private String emailNFe;
	private String emailComercial;
	private String telefonePrimeiro;
	private String telefoneSegundo;
	private String observacao;
	private String motivoInativacao;
	private Boolean nacional;
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_matriz")
	private EntidadeModel matriz;
	private Boolean ativado;
	private Boolean removido;
	
	public EntidadeModel(
			EntidadeRecord dados,
			RegimeTributacaoFederalModel regimeTributacaoFederal,
			SetorAtividadeModel setorAtividade,
			EnderecoModel endereco,
			EntidadeModel matriz
	) {
		this.nomeCompleto = dados.nomeCompleto();
		this.numeroDocumento = dados.numeroDocumento();
		this.inscricaoEstadual = dados.inscricaoEstadual();
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		this.cliente = dados.cliente();
		this.fornecedor = dados.fornecedor();	
		this.parceiro = dados.parceiro();
		this.transportador = dados.transportador();
		this.regimeTributacaoFederal = regimeTributacaoFederal;
		this.setorAtividade = setorAtividade;
		this.endereco = endereco;
		this.contatoPrincipal = dados.contatoPrincipal();
		this.emailNFe = dados.emailNFe();
		this.emailComercial = dados.emailComercial();
		this.telefonePrimeiro = dados.telefonePrimeiro();
		this.telefoneSegundo = dados.telefoneSegundo();
		this.observacao = dados.observacao();
		this.motivoInativacao = dados.motivoInativacao();
		this.nacional = dados.nacional();
		this.matriz = matriz;
		this.ativado = true;
		this.removido = false;
	}
	
	public void atualizar(
			AtualizaEntidadeRecord dados,
			RegimeTributacaoFederalModel regimeTributacaoFederal,
			SetorAtividadeModel setorAtividade,
			EntidadeModel matriz
	) {
		this.nomeCompleto = dados.nomeCompleto();
		this.numeroDocumento = dados.numeroDocumento();
		this.inscricaoEstadual = dados.inscricaoEstadual();
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		this.cliente = dados.cliente();
		this.fornecedor = dados.fornecedor();	
		this.parceiro = dados.parceiro();
		this.transportador = dados.transportador();
		this.regimeTributacaoFederal = regimeTributacaoFederal;
		this.setorAtividade = setorAtividade;
		this.endereco.atualizar(dados.endereco());
		this.contatoPrincipal = dados.contatoPrincipal();
		this.emailNFe = dados.emailNFe();
		this.emailComercial = dados.emailComercial();
		this.telefonePrimeiro = dados.telefonePrimeiro();
		this.telefoneSegundo = dados.telefoneSegundo();
		this.observacao = dados.observacao();
		this.motivoInativacao = dados.motivoInativacao();
		this.nacional = dados.nacional();
		this.matriz = matriz;		
	}
	
}
