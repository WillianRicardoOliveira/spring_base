package com.empresa.erp.domain.old;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizaEnderecoRecord(		
		@Schema(description = "O ID do endereço da entidade é obrigatório e não pode estar vazio ou em branco.", example = "")
		@NotNull(message = "{endereco.id.vazio}")
		Long id,		
		@Schema(description = "O Cep do endereço da entidade é obrigatório e não pode estar vazio ou em branco e pode ter apenas dígitos numéricos e ter no máximo 10 caracteres.", example = "")
		@NotBlank(message = "{endereco.cep.vazio}")
		@Pattern(regexp = "\\d{1,10}", message = "{endereco.cep.tamanho}")	
		String cep,		
		@Schema(description = "A Localidade do endereço da endidade é obrigatório e não pode estar vazio ou em branco e pode ter no máximo 100 caracteres.", example = "")
		@NotBlank(message = "{endereco.localidade.vazio}")
		@Size(max = 100, message = "{endereco.localidade.tamanho}")
		String localidade,				
		@Schema(description = "A UF do endereço da entidade não pode estar vazio ou em branco e deve ter 2 caracteres.", example = "")
		@NotBlank(message="{endereco.uf.vazio}")
		@Size(min = 2, max = 2, message = "{endereco.uf.tamanho}")
	    String uf,	    
	    @Schema(description = "O Bairro do endereço da entidade não pode estar vazio ou em branco e pode ter no máximo 100 caracteres.", example = "")
	    @NotBlank(message="{endereco.bairro.vazio}")
		@Size(max = 100, message = "{endereco.bairro.tamanho}")
		String bairro,		
		@Schema(description = "O Logradouro do endereço da entidade não pode estar vazio ou em branco e pode ter no máximo 100 caracteres.", example = "")
		@NotBlank(message="{endereco.logradouro.vazio}")
		@Size(max = 100, message = "{endereco.logradouro.tamanho}")
		String logradouro,		
		@Schema(description = "O Número do endereço da endidade é obrigatório e não pode estar vazio ou em branco e pode ter apenas dígitos numéricos e ter no máximo 10 caracteres.", example = "")
		@NotBlank(message = "{endereco.numero.vazio}")
		@Pattern(regexp = "\\d{1,10}", message = "{endereco.numero.tamanho}")
		String numero,						
		@Schema(description = "O Complemento do endereço da entidade pode ter no máximo 100 caracteres.", example = "")
		@Size(max = 100, message = "{endereco.complemento.tamanho}")
		String complemento		
) {}