package home.office.spring.domain.estoque.fornecedor.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaFornecedorRecord(
		@NotNull
		Long id,	
		@NotBlank(message = "{cnpj.obrigatorio}")
		String cnpj,
		@NotBlank(message="{nome.obrigatorio}")
		String nome			
) {}
