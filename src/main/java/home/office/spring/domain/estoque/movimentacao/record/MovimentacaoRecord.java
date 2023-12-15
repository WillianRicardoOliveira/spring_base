package home.office.spring.domain.estoque.movimentacao.record;

import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.DetalheTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoRecord(
		@NotNull(message = "{tipo_movimentacao.obrigatorio}")
		DetalheTipoMovimentacaoRecord tipoMovimentacao,
		Long compra,
		@NotNull(message = "{produto.obrigatorio}")
		DetalheProdutoRecord produto,
		@NotNull(message = "{quantidade.obrigatorio}")
		Integer quantidade
) {}
