package home.office.spring.domain.estoque.regimeTributacaoFederal.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaRegimeTributacaoFederalRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{regime_tributacao_federal.nome}")
		String nome
) {}
