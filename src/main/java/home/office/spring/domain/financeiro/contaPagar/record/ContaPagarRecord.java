package home.office.spring.domain.financeiro.contaPagar.record;

import jakarta.validation.constraints.NotBlank;

public record ContaPagarRecord(		
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
