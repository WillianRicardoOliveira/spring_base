package com.empresa.erp.domain.old;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;

@Service
public class CompraService {
	
	@Autowired
	private CompraRepository repository;
	
	@Transactional
	public CompraModel cadastrar(CompraRecord dados) {		
		try {
			var compra = new CompraModel(dados);
			repository.save(compra);		
			return compra;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaCompraRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaCompraRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaCompraRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheCompraRecord atualizar(AtualizaCompraRecord dados) {
		try {
			CompraModel compra = repository.getReferenceById(dados.id());
			compra.atualizar(dados);
			return new DetalheCompraRecord(compra);
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
	
	public DetalheCompraRecord detalhar(Long id) {
		try {
			CompraModel compra = repository.getReferenceById(id);
			return new DetalheCompraRecord(compra);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
