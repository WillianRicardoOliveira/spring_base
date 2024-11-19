package home.office.spring.domain.cadastro.fiscal.entidade.record;

import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntidadeRecord(
		
	@Schema(description = "O nome da entidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 255 caracteres.", example = "Empresa Exemplo LTDA")
	@NotBlank(message = "{entidade.nome.vazio}")	
	@Size(max = 255, message = "{entidade.nome.tamanho}")	
	String nomeCompleto,

    @Schema(description = "O nome fantasia da entidade pode ter no máximo 255 caracteres.", example = "Empresa Exemplo")
	@Size(max = 255, message = "{entidade.nome_fantasia.tamanho}")
	String nomeFantasia,
	 
    @Schema(description = "O número do documento da entidade é obrigatório e não pode estar vazio ou em branco e pode conter apenas dígitos numéricos e ter no máximo 20 caracteres.", example = "12345678901234567890")
	@NotBlank(message = "{entidade.numero_documento.vazio}")	
	@Pattern(regexp = "\\d{1,20}", message = "{entidade.numero_documento.tamanho}")
	String numeroDocumento,
	
	
	
	
	
	
	
	
		
	@Pattern(regexp = "\\d{0,15}", message = "{entidade.inscricao_estadual.digitos}")	
	String inscricaoEstadual,
		
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