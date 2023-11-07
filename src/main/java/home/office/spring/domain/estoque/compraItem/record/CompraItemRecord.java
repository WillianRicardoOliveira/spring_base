package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record CompraItemRecord(			
		
		@NotNull(message = "{compra.obrigatorio}")
		Long compra,
		@NotNull(message = "{fornecedor.obrigatorio}")
		Long fornecedor,	
		@NotNull(message = "{produto.obrigatorio}")
		Long produto,	
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer quantidade,
		@NotNull(message = "{valor.obrigatorio}")
		BigDecimal valor
) {}
