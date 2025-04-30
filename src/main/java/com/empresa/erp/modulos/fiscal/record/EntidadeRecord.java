package com.empresa.erp.modulos.fiscal.record;

import com.empresa.erp.domain.fiscal.endereco.record.EnderecoRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntidadeRecord(
		
	@Schema(description = "A pessoa juridica da entidade é obrigatório e deve ser verdadeiro (true) ou falso (false).", example = "")
	@NotNull(message = "{entidade.pessoa_juridica.vazio}")
	Boolean pessoaJuridica,
		
	@Schema(description = "O nome da entidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 255 caracteres.", example = "")
	@NotBlank(message = "{entidade.nome.vazio}")	
	@Size(max = 255, message = "{entidade.nome.tamanho}")	
	String nomeCompleto,
	
	@Schema(description = "O número do documento da entidade é obrigatório e não pode estar vazio ou em branco e pode conter apenas dígitos numéricos e ter no máximo 20 caracteres.", example = "")
	@NotBlank(message = "{entidade.numero_documento.vazio}")	
	@Pattern(regexp = "\\d{1,20}", message = "{entidade.numero_documento.tamanho}")
	String numeroDocumento,
	
	@Schema(description = "A inscrição estadual da entidade pode conter apenas dígitos numéricos e ter no máximo 15 caracteres.", example = "")
	@Pattern(regexp = "\\d{0,15}", message = "{entidade.inscricao_estadual.digitos}")	
	String inscricaoEstadual,
	
	@Schema(description = "A inscrição municipal da entidade pode conter apenas dígitos numéricos e ter no máximo 15 caracteres.", example = "")
	@Pattern(regexp = "\\d{0,15}", message = "{entidade.inscricao_municipal.digitos}")
	String inscricaoMunicipal,
	
	@Schema(description = "O cliente da entidade é obrigatório e deve ser verdadeiro (true) ou falso (false).", example = "")
	@NotNull(message = "{entidade.cliente.vazio}")
	Boolean cliente,	
	
	@Schema(description = "O fornecedor da entidade é obrigatório e deve ser verdadeiro (true) ou falso (false).", example = "")
	@NotNull(message = "{entidade.fornecedor.vazio}")
	Boolean fornecedor,	
	
	@Schema(description = "O parceiro da entidade é obrigatório e deve ser verdadeiro (true) ou falso (false).", example = "")
	@NotNull(message = "{entidade.parceiro.vazio}")
	Boolean parceiro,	
	
	@Schema(description = "O transportador da entidade é obrigatório e deve ser verdadeiro (true) ou falso (false).", example = "")
	@NotNull(message = "{entidade.transportador.vazio}")
	Boolean transportador,
		
	@Schema(description = "O regime de tributação federal da entidade é obrigatório e não pode estar vazio ou em branco.", example = "")
	@NotNull(message = "{entidade.regime_tributacao_federal.vazio}")
	@Valid
	DetalheRegimeTributacaoFederalRecord regime,
	
	@Schema(description = "O setor de atividade da entidade é obrigatório e não pode estar vazio ou em branco.", example = "")	
	@NotNull(message = "{entidade.setor_atividade.vazio}")
	@Valid
	DetalheSetorAtividadeRecord setor,
	
	@Schema(description = "O endereço da entidade é obrigatório e não pode estar vazio ou em branco.", example = "")	
	@NotNull(message = "{entidade.endereco.vazio}")
	@Valid
	EnderecoRecord endereco,
		
	@Schema(description = "O contato principal é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 255 caracteres.", example = "")
	@NotBlank(message = "{entidade.contato_principal.vazio}")	
	@Size(max = 255, message = "{entidade.contato_principal.tamanho}")
	String contatoPrincipal,
	
	@Schema(description = "O e-mail NFe da entidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 255 caracteres.", example = "")	
	@NotBlank(message = "{entidade.email_nfe.vazio}")	
	@Size(max = 255, message = "{entidade.email_nfe.tamanho}")
	String emailNFe,
	
	@Schema(description = "O e-mail comercial da entidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 255 caracteres.", example = "")	
	@NotBlank(message = "{entidade.email_comercial.vazio}")	
	@Size(max = 255, message = "{entidade.email_comercial.tamanho}")
	String emailComercial,
	
	@Schema(description = "O primeiro telefone da entidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 20 caracteres.", example = "")
	@NotBlank(message = "{entidade.telefone_primeiro.vazio}")	
	@Size(max = 20, message = "{entidade.telefone_primeiro.tamanho}")
	String telefonePrimeiro,
	
	@Schema(description = "O segundo telefone da entidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 20 caracteres.", example = "")
	@NotBlank(message = "{entidade.telefone_segundo.vazio}")	
	@Size(max = 20, message = "{entidade.telefone_segundo.tamanho}")
	String telefoneSegundo,
	
	@Schema(description = "O nacional da entidade é obrigatório e deve ser verdadeiro (true) ou falso (false).", example = "")
	@NotNull(message = "{entidade.nacional.vazio}")
	Boolean nacional,
	
	@Schema(description = "A matriz deve ser um objeto do tipo entidade.", example = "")	
	@Valid
	DetalheEntidadeRecord matriz
	
) {}