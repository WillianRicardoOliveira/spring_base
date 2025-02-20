package com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaRegimeTributacaoFederalRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{regime_tributacao_federal.nome}")
		String nome
) {}
