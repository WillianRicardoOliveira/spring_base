package com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record;

import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DetalheRegimeTributacaoFederalRecord(
		@NotNull(message = "{regime_tributacao_federal.id}")
		Long id,
		@NotBlank(message = "{regime_tributacao_federal.nome}")
		String nome
) {
		
	public DetalheRegimeTributacaoFederalRecord(RegimeTributacaoFederalModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
				
	}

}