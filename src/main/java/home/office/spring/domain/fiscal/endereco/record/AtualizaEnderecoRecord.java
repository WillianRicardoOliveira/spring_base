package home.office.spring.domain.fiscal.endereco.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaEnderecoRecord(
		@NotNull
		Long id,
		@NotBlank(message="{endereco.cep}")
		String cep,
		@NotBlank(message="{endereco.localidade}")
		String localidade,
		@NotBlank(message="{endereco.uf}")
	    String uf,
	    @NotBlank(message="{endereco.bairro}")
		String bairro,
		@NotBlank(message="{endereco.logradouro}")
		String logradouro,
		@NotBlank(message="{endereco.numero}")
		String numero,
		String complemento
) {}
