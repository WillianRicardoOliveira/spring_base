package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaFornecedorRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{fornecedor.cnpj}")
		String cnpj,
		@NotBlank(message = "{fornecedor.razao_social}")
		String razaoSocial,
		String nomeFantasia,
		String inscricaoEstadual,
		String inscricaoMunicipal,		
		@NotNull(message = "{fornecedor.regime_tributacao_federal}")
		DetalheRegimeTributacaoFederalRecord regimeTributacaoFederal,
		@NotNull(message = "{fornecedor.setor_atividade}")
		DetalheSetorAtividadeRecord setorAtividade
) {}
