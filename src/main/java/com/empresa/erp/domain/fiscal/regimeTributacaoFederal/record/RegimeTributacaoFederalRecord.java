package com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record;

import jakarta.validation.constraints.NotBlank;

public record RegimeTributacaoFederalRecord(
		@NotBlank(message = "{regime_tributacao_federal.nome}")
		String nome	
) {}
