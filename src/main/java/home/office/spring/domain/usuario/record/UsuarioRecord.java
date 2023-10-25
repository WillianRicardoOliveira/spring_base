package home.office.spring.domain.usuario.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRecord(
	@NotBlank(message = "{email.obrigatorio}")
	@Email(message = "{email.invalido}")
	String email,
	@NotBlank(message = "{senha.obrigatorio}")
	String senha		
) {}