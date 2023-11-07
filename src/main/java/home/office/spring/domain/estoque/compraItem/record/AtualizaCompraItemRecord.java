package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record AtualizaCompraItemRecord(
		@NotNull
		Long id,
		@NotNull(message = "{compra.obrigatorio}")
		Long fornecedor,	
		@NotNull(message = "{produto.obrigatorio}")
		Long produto,	
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer quantidade,
		@NotNull(message = "{valor.obrigatorio}")
		BigDecimal valor
) {}
