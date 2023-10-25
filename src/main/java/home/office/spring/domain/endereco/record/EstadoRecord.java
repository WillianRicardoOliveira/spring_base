package home.office.spring.domain.endereco.record;

import jakarta.validation.constraints.NotBlank;

public record EstadoRecord(
	@NotBlank(message="{estado.nome}")
	String nome,
	@NotBlank(message="{estado.sigla}")
	String sigla
) {}
