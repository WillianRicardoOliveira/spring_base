package home.office.spring.domain.estoque.compraItem.record;

import java.math.BigDecimal;

import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;
import jakarta.validation.constraints.NotNull;

public record AtualizaCompraItemRecord(
		@NotNull
		Long id,
		@NotNull(message = "{compra.obrigatorio}")
		DetalheFornecedorRecord fornecedor,	
		@NotNull(message = "{produto.obrigatorio}")
		DetalheProdutoRecord produto,	
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer quantidade,
		@NotNull(message = "{valor.obrigatorio}")
		BigDecimal valor
) {}
