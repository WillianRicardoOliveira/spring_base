package com.empresa.erp.domain.old;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;

@Service
public class ContaService {
	
	@Autowired
	private ContaRepository repository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Transactional
	public ContaModel cadastrar(ContaRecord dados) {		
		try {
			BancoModel banco = bancoRepository.getReferenceById(dados.banco().id());
			var conta = new ContaModel(dados, banco);
			repository.save(conta);		
			return conta;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaContaRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByContaContaining(paginacao, filtro).map(ListaContaRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaContaRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheContaRecord atualizar(AtualizaContaRecord dados) {
		try {
			BancoModel banco = bancoRepository.getReferenceById(dados.banco().id());
			ContaModel conta = repository.getReferenceById(dados.id());
			conta.atualizar(dados, banco);
			return new DetalheContaRecord(conta);
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
	
	public DetalheContaRecord detalhar(Long id) {
		try {
			ContaModel conta = repository.getReferenceById(id);
			return new DetalheContaRecord(conta);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
