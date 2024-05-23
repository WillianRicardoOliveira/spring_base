package home.office.spring.domain.estoque.regimeTributacaoFederal.record;

import jakarta.validation.constraints.NotBlank;

public record RegimeTributacaoFederalRecord(
		@NotBlank(message = "{regime_tributacao_federal.nome}")
		String nome	
) {}
