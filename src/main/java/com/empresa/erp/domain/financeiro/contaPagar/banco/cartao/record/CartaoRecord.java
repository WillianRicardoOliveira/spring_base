package com.empresa.erp.domain.financeiro.contaPagar.banco.cartao.record;

import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartaoRecord(
		@NotNull(message = "{conta.obrigatorio}")
		Long conta,
		@NotNull(message = "{formaPagamento.obrigatorio}")
		DetalheFormaPagamentoRecord formaPagamento,		
		@NotBlank(message = "{numeroCartao.obrigatorio}")
		String numeroCartao,
		@NotBlank(message = "{validadeMes.obrigatorio}")
		String validadeMes,
		@NotBlank(message = "{validadeAno.obrigatorio}")
		String validadeAno			
) {}
