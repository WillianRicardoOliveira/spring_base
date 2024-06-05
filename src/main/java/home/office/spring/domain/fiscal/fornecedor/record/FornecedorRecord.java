package home.office.spring.domain.fiscal.fornecedor.record;

import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FornecedorRecord(
		
	@NotNull(message = "{fornecedor.tipo}")
	Boolean tipo,
		
	@NotBlank(message = "{fornecedor.numero_documento}")
	String numeroDocumento,
		
	@NotBlank(message = "{fornecedor.razao_social}")
	String razaoSocial,
		
	String nomeFantasia,
		
	String inscricaoEstadual,
		
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