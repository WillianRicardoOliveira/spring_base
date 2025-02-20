package com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record;

import jakarta.validation.constraints.NotBlank;

public record FormaPagamentoRecord(
		@NotBlank(message = "{nome.obrigatorio}")
		String nome
) {}
