package com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record;

import jakarta.validation.constraints.NotBlank;

public record StatusPagamentoRecord(
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
