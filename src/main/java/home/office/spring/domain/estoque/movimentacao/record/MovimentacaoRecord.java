package home.office.spring.domain.estoque.movimentacao.record;

import home.office.spring.domain.estoque.movimentacao.constante.TipoMovimentacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoRecord(
		@NotBlank(message = "{tipo_movimentacao.obrigatorio}")
		TipoMovimentacao tipoMovimentacao,
		Long compra,
		Long cliente,
		@NotNull(message = "{produto.obrigatorio}")
		Long produto,
		@NotBlank(message = "{quantidade.obrigatorio}")
		Integer quantidade,
		@NotNull(message = "{total.obrigatorio}")
		Integer total
) {}