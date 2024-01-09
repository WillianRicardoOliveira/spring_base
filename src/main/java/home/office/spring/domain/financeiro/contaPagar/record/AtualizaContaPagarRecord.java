package home.office.spring.domain.financeiro.contaPagar.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaContaPagarRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
