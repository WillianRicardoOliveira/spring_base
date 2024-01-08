package home.office.spring.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubCategoriaContaRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,
		
		@NotNull(message = "{obrigatorio}")
		Long categoriaConta
		
) {}
