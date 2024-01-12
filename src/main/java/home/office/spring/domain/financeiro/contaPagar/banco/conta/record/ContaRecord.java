package home.office.spring.domain.financeiro.contaPagar.banco.conta.record;

import home.office.spring.domain.financeiro.contaPagar.banco.record.DetalheBancoRecord;
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
