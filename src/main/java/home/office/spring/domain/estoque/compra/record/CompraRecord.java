package home.office.spring.domain.estoque.compra.record;

import jakarta.validation.constraints.NotBlank;

public record CompraRecord(			
		@NotBlank(message = "{descricao.obrigatorio}")
		String descricao	
) {}
