package home.office.spring.domain.endereco.record;

import home.office.spring.domain.endereco.model.EnderecoModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EnderecoRecord(	
	@NotBlank(message="{logradouro.obrigatorio}")
	String logradouro,
	@NotBlank(message="{bairro.obrigatorio}")
	String bairro,
	@NotBlank(message="{cep.obrigatorio}")
	@Pattern(regexp = "\\d{8}", message = "{cep.invalido}")
	String cep,	
	@NotNull(message="{estado.obrigatorio}")
	@Valid
	EstadoRecord estado,
	@NotBlank(message="{cidade.obrigatorio}")
    String cidade,
	@NotBlank(message="{numero.obrigatorio}")
	String numero,
	String complemento
) {
	public EnderecoRecord(EnderecoModel dados) {
		
		this(
				dados.getLogradouro(),
				dados.getBairro(),
				dados.getCep(),
				new EstadoRecord(dados.getEstado()),
				dados.getCidade(),
				dados.getNumero(),
				dados.getComplemento()
				);
		
	}
}
