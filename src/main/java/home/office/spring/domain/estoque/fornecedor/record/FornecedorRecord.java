package home.office.spring.domain.estoque.fornecedor.record;

import jakarta.validation.constraints.NotBlank;

public record FornecedorRecord(			
		@NotBlank(message = "{cnpj.obrigatorio}")
		String cnpj,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,
		@NotBlank(message = "{telefone.obrigatorio}")
		String telefone,
		String descricao
) {}
