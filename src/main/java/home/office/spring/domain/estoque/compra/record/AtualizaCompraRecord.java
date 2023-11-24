package home.office.spring.domain.estoque.compra.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaCompraRecord(
		@NotNull
		Long id,
		@NotBlank(message = "{descricao.obrigatorio}")
		String descricao
) {}
