package com.empresa.erp.domain.old;

import jakarta.validation.constraints.NotBlank;

public record ContaRecord(
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
