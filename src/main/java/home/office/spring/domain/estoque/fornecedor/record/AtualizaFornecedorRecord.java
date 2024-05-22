package home.office.spring.domain.estoque.fornecedor.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaFornecedorRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{fornecedor.cnpj}")
		String cnpj,
		@NotBlank(message = "{fornecedor.razao_social}")
		String razaoSocial,
		String nomeFantasia
) {}
