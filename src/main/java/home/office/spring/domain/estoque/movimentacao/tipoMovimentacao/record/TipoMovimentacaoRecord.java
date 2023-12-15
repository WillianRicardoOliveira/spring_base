package home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record;

import jakarta.validation.constraints.NotNull;

public record TipoMovimentacaoRecord(
		@NotNull(message = "{nome.obrigatorio}")
		String nome
) {}
