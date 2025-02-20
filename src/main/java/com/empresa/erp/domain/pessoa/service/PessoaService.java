package com.empresa.erp.domain.pessoa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;
import com.empresa.erp.domain.fiscal.endereco.repository.EnderecoRepository;
import com.empresa.erp.domain.pessoa.model.PessoaModel;
import com.empresa.erp.domain.pessoa.record.AtualizaPessoaRecord;
import com.empresa.erp.domain.pessoa.record.DetalhePessoaRecord;
import com.empresa.erp.domain.pessoa.record.ListaPessoaRecord;
import com.empresa.erp.domain.pessoa.record.PessoaRecord;
import com.empresa.erp.domain.pessoa.repository.PessoaRepository;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

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
