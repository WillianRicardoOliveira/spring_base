package home.office.spring.domain.fiscal.setorAtividade.record;

import home.office.spring.domain.fiscal.setorAtividade.model.SetorAtividadeModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DetalheSetorAtividadeRecord(
		@NotNull(message = "{setor_atividade.id}")
		Long id,
		@NotBlank(message = "{setor_atividade.nome}")
		String nome
) {
		
	public DetalheSetorAtividadeRecord(SetorAtividadeModel dados) {
		
		this(
				dados.getId(),
				dados.getNome()
		);
				
	}

}