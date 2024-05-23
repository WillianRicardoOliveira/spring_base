package home.office.spring.domain.estoque.setorAtividade.record;

import jakarta.validation.constraints.NotBlank;

public record SetorAtividadeRecord(
		@NotBlank(message = "{setor_atividade.nome}")
		String nome	
) {}
