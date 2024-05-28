package home.office.spring.domain.fiscal.regimeTributacaoFederal.record;

import jakarta.validation.constraints.NotBlank;

public record RegimeTributacaoFederalRecord(
		@NotBlank(message = "{regime_tributacao_federal.nome}")
		String nome	
) {}
