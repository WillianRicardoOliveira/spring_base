package home.office.spring.domain.cadastro.fiscal.entidade.record;

import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntidadeRecord(
		
	@NotBlank(message = "{entidade.nome}")	
	@Size(min = 1, max = 255, message = "{entidade.nome.tamanho}")	
	String nomeCompleto,
		
	@Size(max = 255, message = "{entidade.nome_fantasia.tamanho}")
	String nomeFantasia,
	
	@NotBlank(message = "{entidade.numero_documento}")	
	@Pattern(regexp = "\\d{1,20}", message = "{entidade.numero_documento.digitos}")
	@Size(min = 1, max = 20, message = "{entidade.numero_documento.tamanho}")
	String numeroDocumento,
		
	@Size(max = 15, message = "{entidade.inscricao_estadual.tamanho}")	
	@Pattern(regexp = "\\d{0,15}", message = "{entidade.inscricao_estadual.digitos}")	
	String inscricaoEstadual,
	
	@Size(max = 15, message = "{entidade.inscricao_municipal.tamanho}")
	@Pattern(regexp = "\\d{0,15}", message = "{entidade.inscricao_municipal.digitos}")
	String inscricaoMunicipal,
		
	@NotNull(message = "{entidade.regime_tributacao_federal}")
	@Valid
	DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,	
	
	@NotNull(message = "{entidade.setor_atividade}")
	@Valid
	DetalheSetorAtividadeRecord setorAtividade,	
	
	@NotNull(message = "{entidade.endereco}")
	@Valid
	EnderecoRecord endereco	
	
) {}