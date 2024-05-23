package home.office.spring.domain.estoque.setorAtividade.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaSetorAtividadeRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{setor_atividade.nome}")
		String nome
) {}
