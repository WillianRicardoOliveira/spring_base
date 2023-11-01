package home.office.spring.domain.estoque.produto.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizaProdutoRecord(
	@NotNull
	Long id,
	@NotBlank(message = "{nome.obrigatorio}")
	String nome,
	@NotBlank(message = "{descricao.obrigatorio}")
	String descricao,
	@NotNull(message = "{quantidade.obrigatorio}")
	Integer quantidade,
	@NotNull(message = "{minimo.obrigatorio}")
	Integer minimo,
	@NotNull(message = "{maxino.obrigatorio}")
	Integer maximo
) {}