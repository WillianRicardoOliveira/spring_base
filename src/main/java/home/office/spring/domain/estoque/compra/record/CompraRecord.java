package home.office.spring.domain.estoque.compra.record;

import home.office.spring.domain.estoque.compra.constante.Status;
import jakarta.validation.constraints.NotBlank;

public record CompraRecord(			
		@NotBlank(message = "{descricao.obrigatorio}")
		String descricao,
		@NotBlank(message = "{status.obrigatorio}")
		Status status,
		@NotBlank(message = "{data.obrigatorio}")
		String data			
) {}
