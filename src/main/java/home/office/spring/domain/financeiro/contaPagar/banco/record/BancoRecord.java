package home.office.spring.domain.financeiro.contaPagar.banco.record;

import jakarta.validation.constraints.NotBlank;

public record BancoRecord(		
		@NotBlank(message = "{nome.obrigatorio}")		
		String nome,
		@NotBlank(message = "{agencia.obrigatorio}")
		String agencia,
		@NotBlank(message = "{conta.obrigatorio}")
		String conta,
		@NotBlank(message = "{digito.obrigatorio}")
		String digito,		
		String pix		
) {}
