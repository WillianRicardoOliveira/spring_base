package home.office.spring.domain.estoque.fornecedor.record;

import jakarta.validation.constraints.NotBlank;

public record FornecedorRecord(
		@NotBlank(message = "{fornecedor.cnpj}")
		String cnpj,
		@NotBlank(message = "{fornecedor.razao_social}")
		String razaoSocial,
		String nomeFantasia		
) {}
