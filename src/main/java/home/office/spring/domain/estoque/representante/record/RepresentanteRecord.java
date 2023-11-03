package home.office.spring.domain.estoque.representante.record;

import home.office.spring.domain.estoque.fornecedor.record.FornecedorRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RepresentanteRecord(			
		@NotBlank(message = "{nome.obrigatorio}")
		String nome,
		@NotBlank(message="{celular.obrigatorio}")
		String celular,
		@NotNull
		@Valid
		FornecedorRecord fornecedor
) {}
