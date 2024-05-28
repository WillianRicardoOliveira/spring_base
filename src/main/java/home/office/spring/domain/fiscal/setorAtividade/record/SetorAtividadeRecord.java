package home.office.spring.domain.fiscal.setorAtividade.record;

import jakarta.validation.constraints.NotBlank;

public record SetorAtividadeRecord(
		@NotBlank(message = "{setor_atividade.nome}")
		String nome	
) {}
