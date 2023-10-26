package home.office.spring.domain.endereco.record;

import home.office.spring.domain.endereco.model.EstadoModel;
import jakarta.validation.constraints.NotBlank;

public record EstadoRecord(
	@NotBlank(message="{estado.nome}")
	String nome,
	@NotBlank(message="{estado.sigla}")
	String sigla
) {
	public EstadoRecord (EstadoModel dados) {
		this(dados.getNome(), dados.getSigla());
	}
}
