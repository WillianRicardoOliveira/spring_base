package home.office.spring.domain.financeiro.contaPagar.banco.record;

import jakarta.validation.constraints.NotBlank;

public record BancoRecord(		
		@NotBlank(message = "{nome.obrigatorio}")		
		String nome
) {}
