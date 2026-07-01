package com.empresa.erp.domain.old;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class EntidadeModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Boolean pessoaJuridica;
	private String nomeCompleto;
	@Column(unique = true)
	private String numeroDocumento;
	private String inscricaoEstadual;
	private String inscricaoMunicipal;
	private Boolean cliente;
	private Boolean fornecedor;
	private Boolean parceiro;
	private Boolean transportador;
	@OneToOne
	@JoinColumn(name = "id_regime_tributacao_federal")
	private RegimeTributacaoFederalModel regimeTributacaoFederal;
	@OneToOne
	@JoinColumn(name = "id_setor_atividade")
	private SetorAtividadeModel setorAtividade;
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "id_endereco")
	private EnderecoModel endereco;
	private String contatoPrincipal;
	private String emailNfe;
	private String emailComercial;
	private String primeiroTelefone;
	private String segundoTelefone;
	private Boolean nacional;
	@OneToOne
	@JoinColumn(name = "id_matriz")
	private EntidadeModel matriz;
	@Enumerated(EnumType.ORDINAL)
	private StatusEnum status;
	public EntidadeModel(EntidadeRecord dados, RegimeTributacaoFederalModel regime, SetorAtividadeModel setor, EnderecoModel endereco, EntidadeModel matriz) {
		this.pessoaJuridica = dados.pessoaJuridica();
		this.nomeCompleto = dados.nomeCompleto();
		this.numeroDocumento = dados.numeroDocumento();
		this.inscricaoEstadual = dados.inscricaoEstadual();
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		this.cliente = dados.cliente();
		this.fornecedor = dados.fornecedor();
		this.parceiro = dados.parceiro();
		this.transportador = dados.transportador();
		this.regimeTributacaoFederal = regime;
		this.setorAtividade = setor;
		this.endereco = endereco;
		this.contatoPrincipal = dados.contatoPrincipal();
		this.emailNfe = dados.emailNfe();
		this.emailComercial = dados.emailComercial();
		this.primeiroTelefone = dados.primeiroTelefone();
		this.segundoTelefone = dados.segundoTelefone();
		this.nacional = dados.nacional();
		this.matriz = matriz;
		this.status = StatusEnum.ATIVO;
	}
	public void atualizar(AtualizaEntidadeRecord dados, RegimeTributacaoFederalModel regime, SetorAtividadeModel setor, EntidadeModel matriz) {
		this.pessoaJuridica = dados.pessoaJuridica();
		this.nomeCompleto = dados.nomeCompleto();
		this.numeroDocumento = dados.numeroDocumento();
		this.inscricaoEstadual = dados.inscricaoEstadual();
		this.inscricaoMunicipal = dados.inscricaoMunicipal();
		this.cliente = dados.cliente();
		this.fornecedor = dados.fornecedor();
		this.parceiro = dados.parceiro();
		this.transportador = dados.transportador();
		this.regimeTributacaoFederal = regime;
		this.setorAtividade = setor;
		this.endereco.atualizar(dados.endereco());
		this.contatoPrincipal = dados.contatoPrincipal();
		this.emailNfe = dados.emailNfe();
		this.emailComercial = dados.emailComercial();
		this.primeiroTelefone = dados.primeiroTelefone();
		this.segundoTelefone = dados.segundoTelefone();
		this.nacional = dados.nacional();
		this.matriz = matriz;
	}
}