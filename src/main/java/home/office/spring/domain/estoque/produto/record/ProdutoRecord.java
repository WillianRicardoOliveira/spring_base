package home.office.spring.domain.estoque.produto.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRecord(
	@NotBlank(message = "{nome.obrigatorio}")
	String nome,
	@NotBlank(message = "{descricao.obrigatorio}")
	String descricao,
	@NotNull(message = "{quantidade.obrigatorio}")
	Integer minimo,
	@NotNull(message = "{maxino.obrigatorio}")
	Integer maximo
) {}
