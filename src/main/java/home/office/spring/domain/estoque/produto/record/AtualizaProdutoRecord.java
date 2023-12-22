package home.office.spring.domain.estoque.produto.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaProdutoRecord(
	@NotNull
	Long id,
	@NotBlank(message = "{nome.obrigatorio}")
	String nome,
	String descricao,
	@NotNull(message = "{minimo.obrigatorio}")
	Integer minimo,
	@NotNull(message = "{maxino.obrigatorio}")
	Integer maximo
) {}