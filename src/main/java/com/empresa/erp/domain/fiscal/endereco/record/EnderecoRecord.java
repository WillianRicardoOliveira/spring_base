package com.empresa.erp.domain.fiscal.endereco.record;

import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRecord(		
		@NotBlank(message = "{endereco.cep}")
		@Pattern(regexp = "\\d{1,10}", message = "{endereco.cep.digitos}")
		@Size(max = 10, message = "{endereco.cep.tamanho}")
		String cep,
		
		@NotBlank(message = "{endereco.localidade}")
		@Size(max = 100, message = "{endereco.localidade.tamanho}")
		String localidade,
		
		@NotBlank(message="{endereco.uf}")
		@Size(min = 2, max = 2, message = "{endereco.uf.tamanho}")
		String uf,
		
	    @NotBlank(message="{endereco.bairro}")
		@Size(max = 100, message = "{endereco.bairro.tamanho}")
		String bairro,
		
		@NotBlank(message="{endereco.logradouro}")
		@Size(max = 100, message = "{endereco.logradouro.tamanho}")
		String logradouro,
		
		@NotBlank(message="{endereco.numero}")
		@Pattern(regexp = "\\d{1,10}", message = "{endereco.numero.digitos}")
		@Size(max = 10, message = "{endereco.numero.tamanho}")
		String numero,
		
		@Size(max = 100, message = "{endereco.complemento.tamanho}")
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
