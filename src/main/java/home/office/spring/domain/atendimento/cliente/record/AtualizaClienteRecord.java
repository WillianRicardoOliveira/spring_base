package home.office.spring.domain.atendimento.cliente.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaClienteRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,
		@NotBlank(message = "{telefone.obrigatorio}")
		String telefone
) {}
