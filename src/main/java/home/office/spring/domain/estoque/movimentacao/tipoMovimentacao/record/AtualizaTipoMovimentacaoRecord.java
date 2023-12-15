package home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record;

import jakarta.validation.constraints.NotNull;

public record AtualizaTipoMovimentacaoRecord(
		@NotNull
		Long id,
		@NotNull(message = "{nome.obrigatorio}")
		String nome
) {}
