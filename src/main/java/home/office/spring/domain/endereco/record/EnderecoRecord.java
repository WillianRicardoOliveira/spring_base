package home.office.spring.domain.endereco.record;

import home.office.spring.domain.endereco.model.EnderecoModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EnderecoRecord(	
	@NotBlank(message="{cep.obrigatorio}")
	@Pattern(regexp = "\\d{8}", message = "{cep.invalido}")
	String cep,
	@NotBlank(message="{logradouro.obrigatorio}")
	String logradouro,
	String complemento,
	@NotBlank(message="{bairro.obrigatorio}")
	String bairro,
	@NotNull(message="{localidade.obrigatorio}")
	@Valid
	String localidade,
	@NotBlank(message="{uf.obrigatorio}")
    String uf,
	@NotBlank(message="{numero.obrigatorio}")
	String numero
	
) {
	public EnderecoRecord(EnderecoModel dados) {
		
		this(
				dados.getCep(),
				dados.getLogradouro(),
				dados.getComplemento(),
				dados.getBairro(),
				dados.getLocalidade(),
				dados.getUf(),
				dados.getNumero()				
				);
		
	}
}
