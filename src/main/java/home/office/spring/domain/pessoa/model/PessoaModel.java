package home.office.spring.domain.pessoa.model;

import home.office.spring.domain.endereco.model.EnderecoModel;
import home.office.spring.domain.pessoa.constante.TipoPessoa;
import home.office.spring.domain.pessoa.record.AtualizaPessoaRecord;
import home.office.spring.domain.pessoa.record.PessoaRecord;
import home.office.spring.domain.usuario.model.UsuarioModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "pessoa")
@Entity(name = "PessoaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PessoaModel {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String nascimento;
	private String genero;
	private String cpf;
	private String telefone;	
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_endereco")
	private EnderecoModel endereco;	
	@OneToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "id_usuario")
	private UsuarioModel usuario;
	private Boolean aceiteTermo;
	@Enumerated(EnumType.STRING)
	private TipoPessoa tipoPessoa;
	private Boolean ativo;
	
	public PessoaModel(PessoaRecord dados, EnderecoModel endereco, UsuarioModel usuario) {
		this.nome = dados.nome();
		this.nascimento = dados.nascimento();
		this.genero = dados.genero();
		this.cpf = dados.cpf();
		this.telefone = dados.telefone();
		this.endereco = endereco;
		this.usuario = usuario;
		this.aceiteTermo = dados.aceiteTermo();
		this.tipoPessoa = dados.tipoPessoa();
		this.ativo = true;
	}
	
	public void atualizar(AtualizaPessoaRecord dados) {
		if(dados.nome() != null) {
			this.nome = dados.nome();
		}
		if(dados.nascimento() != null) {
			this.nascimento = dados.nascimento();
		}
		if(dados.genero() != null) {
			this.genero = dados.genero();
		}
		if(dados.cpf() != null) {
			this.cpf = dados.cpf();
		}
		if(dados.telefone() != null) {
			this.telefone = dados.telefone();
		}
		if(dados.endereco() != null) {
			this.endereco.atualizar(dados.endereco());
		}
		if(dados.usuario() != null) {
			this.usuario.atualizar(dados.usuario());
		}
		
	}
	
	public void inativar() {
		this.ativo = false;
	}
		
}
