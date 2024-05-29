package home.office.spring.domain.fiscal.endereco.record;

import home.office.spring.domain.fiscal.endereco.model.EnderecoModel;
import jakarta.validation.constraints.NotBlank;

public record EnderecoRecord(	
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
) {
	public EnderecoRecord(EnderecoModel dados) {
		
		this(
				dados.getCep(),
				dados.getLocalidade(),
				dados.getUf(),
				dados.getBairro(),
				dados.getLogradouro(),
				dados.getNumero(),
				dados.getComplemento()
		);
		
	}
}
