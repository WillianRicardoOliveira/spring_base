package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record;

import jakarta.validation.constraints.NotBlank;

public record SubCategoriaContaRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
