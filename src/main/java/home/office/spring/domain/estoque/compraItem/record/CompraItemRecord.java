package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;
import home.office.spring.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;
import jakarta.validation.constraints.NotNull;

public record CompraItemRecord(			
		
		@NotNull(message = "{compra.obrigatorio}")
		Long compra,
		@NotNull(message = "{fornecedor.obrigatorio}")
		DetalheFornecedorRecord fornecedor,	
		@NotNull(message = "{produto.obrigatorio}")
		DetalheProdutoRecord produto,	
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer quantidade,
		@NotNull(message = "{valor.obrigatorio}")
		BigDecimal valor
) {}
