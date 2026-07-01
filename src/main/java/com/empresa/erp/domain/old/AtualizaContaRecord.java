package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaContaRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{banco.obrigatorio}")		
		DetalheBancoRecord banco,
		@NotBlank(message = "{agencia.obrigatorio}")
		String agencia,
		@NotBlank(message = "{conta.obrigatorio}")
		String conta,
		@NotBlank(message = "{digito.obrigatorio}")
		String digito,		
		String pix
) {}
