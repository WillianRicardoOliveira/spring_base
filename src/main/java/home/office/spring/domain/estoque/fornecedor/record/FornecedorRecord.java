package home.office.spring.domain.estoque.fornecedor.record;

import home.office.spring.domain.estoque.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import home.office.spring.domain.estoque.setorAtividade.record.DetalheSetorAtividadeRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FornecedorRecord(
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
