package home.office.spring.domain.estoque.fornecedor.record;

import jakarta.validation.constraints.NotBlank;

public record AtualizaFornecedorRecord(
		@NotBlank(message = "{cnpj.obrigatorio}")
		String cnpj,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome		
) {}
