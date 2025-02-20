package com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaFormaPagamentoRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
