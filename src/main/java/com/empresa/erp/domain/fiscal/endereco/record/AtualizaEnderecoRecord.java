package com.empresa.erp.domain.fiscal.endereco.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizaEnderecoRecord(
		@NotNull(message = "{endereco.id}")
		Long id,
		
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
		@Size(max = 150, message = "{endereco.logradouro.tamanho}")
		String logradouro,
		
		@NotBlank(message="{endereco.numero}")
		@Pattern(regexp = "\\d{1,10}", message = "{endereco.numero.digitos}")
		@Size(max = 10, message = "{endereco.numero.tamanho}")
		String numero,
		
		@Size(max = 100, message = "{endereco.complemento.tamanho}")
		String complemento
) {}