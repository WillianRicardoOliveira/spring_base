package com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaStatusPagamentoRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
