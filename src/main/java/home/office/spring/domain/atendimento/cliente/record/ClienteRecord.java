package home.office.spring.domain.atendimento.cliente.record;

import jakarta.validation.constraints.NotBlank;

public record ClienteRecord(			
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,
		@NotBlank(message = "{telefone.obrigatorio}")
		String telefone
) {}
