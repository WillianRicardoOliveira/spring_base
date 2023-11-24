package home.office.spring.domain.pessoa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.endereco.model.EnderecoModel;
import home.office.spring.domain.endereco.repository.EnderecoRepository;
import home.office.spring.domain.pessoa.model.PessoaModel;
import home.office.spring.domain.pessoa.record.AtualizaPessoaRecord;
import home.office.spring.domain.pessoa.record.DetalhePessoaRecord;
import home.office.spring.domain.pessoa.record.ListaPessoaRecord;
import home.office.spring.domain.pessoa.record.PessoaRecord;
import home.office.spring.domain.pessoa.repository.PessoaRepository;
import home.office.spring.domain.usuario.model.UsuarioModel;
import home.office.spring.domain.usuario.repository.UsuarioRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository repository;

	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Transactional
	public PessoaModel cadastrar(PessoaRecord dados) {		
		try {
			var endereco = new EnderecoModel(dados.endereco());
			enderecoRepository.save(endereco);		
			var usuario = new UsuarioModel(dados.usuario());
			usuarioRepository.save(usuario);		
			var pessoa = new PessoaModel(dados, endereco, usuario);
			repository.save(pessoa);		
			return pessoa;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaPessoaRecord> listar(Pageable paginacao) {
		try {
			return repository.findAllByAtivoTrue(paginacao).map(ListaPessoaRecord::new);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalhePessoaRecord atualizar(AtualizaPessoaRecord dados) {
		try {
			PessoaModel pessoa = repository.getReferenceById(dados.id());
			pessoa.atualizar(dados);
			return new DetalhePessoaRecord(pessoa);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização.");
		}
	}
	
	@Transactional
	public void excluir(Long id) {
		try {
			repository.getReferenceById(id).inativar();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão.");
		}
	}
	
	public DetalhePessoaRecord detalhar(Long id) {
		try {
			PessoaModel pessoa = repository.getReferenceById(id);
			return new DetalhePessoaRecord(pessoa);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
