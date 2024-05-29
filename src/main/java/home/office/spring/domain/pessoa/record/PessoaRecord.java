package home.office.spring.domain.pessoa.record;

import home.office.spring.domain.fiscal.endereco.record.EnderecoRecord;
import home.office.spring.domain.pessoa.constante.TipoPessoa;
import home.office.spring.domain.usuario.record.UsuarioRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PessoaRecord(
	@NotBlank(message = "{nome.obrigatorio}")
	String nome,
	@NotBlank(message="{nascimento.obrigatorio}")
	String nascimento,
	@NotBlank(message="{genero.obrigatorio}")
	String genero,
	@NotBlank(message="{cpf.obrigatorio}")
	String cpf,
	@NotBlank(message="{telefone.obrigatorio}")
	String telefone,
	@NotNull(message="{endereco.obrigatorio}")
	@Valid
	EnderecoRecord endereco,
	@NotNull(message="{usuario.obrigatorio}")
	@Valid
	UsuarioRecord usuario,
	@NotNull(message="{aceitarTermos.obrigatorio}")
	Boolean aceitarTermos,
	@NotNull(message = "{tipoPessoa.obrigatorio}")
	TipoPessoa tipoPessoa
) {}
