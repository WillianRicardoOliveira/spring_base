package home.office.spring.domain.fiscal.fornecedor.record;

import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FornecedorRecord(
	@NotNull(message = "{fornecedor.tipo}")
	Boolean tipo,
	@NotBlank(message = "{fornecedor.numero_documento}")	
	@Pattern(regexp = "\\d{1,14}", message = "{fornecedor.numero_documento.digitos}")
	@Size(min = 1, max = 14, message = "{fornecedor.numero_documento.tamanho}")
	String numeroDocumento,
	@NotBlank(message = "{fornecedor.razao_social}")
	@Size(min = 1, max = 100, message = "{fornecedor.razao_social.tamanho}")
	String razaoSocial,
	@Size(max = 100, message = "{fornecedor.nome_fantasia.tamanho}")
	String nomeFantasia,
	@Size(max = 14, message = "{fornecedor.inscricao_estadual.tamanho}")
	@Pattern(regexp = "\\d{0,14}", message = "{fornecedor.inscricao_estadual.digitos}")
	String inscricaoEstadual,
	@Size(max = 14, message = "{fornecedor.inscricao_municipal.tamanho}")
	@Pattern(regexp = "\\d{0,14}", message = "{fornecedor.inscricao_municipal.digitos}")
	String inscricaoMunicipal,
	@NotNull(message = "{fornecedor.regime_tributacao_federal}")
	@Valid
	DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,
	@NotNull(message = "{fornecedor.setor_atividade}")
	@Valid
	DetalheSetorAtividadeRecord setorAtividade,
	@NotNull(message = "{fornecedor.endereco}")
	@Valid
	EnderecoRecord endereco	
) {}