package com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.AtualizaCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.CategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.DetalheCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.ListaCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.repository.CategoriaContaRepository;

@Service
public class CategoriaContaService {
	
	@Autowired
	private CategoriaContaRepository repository;
	
	@Transactional
	public CategoriaContaModel cadastrar(CategoriaContaRecord dados) {		
		try {
			var categoriaConta = new CategoriaContaModel(dados);
			repository.save(categoriaConta);		
			return categoriaConta;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaCategoriaContaRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaCategoriaContaRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaCategoriaContaRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheCategoriaContaRecord atualizar(AtualizaCategoriaContaRecord dados) {
		try {
			CategoriaContaModel categoriaConta = repository.getReferenceById(dados.id());
			categoriaConta.atualizar(dados);
			return new DetalheCategoriaContaRecord(categoriaConta);
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
	
	public DetalheCategoriaContaRecord detalhar(Long id) {
		try {
			CategoriaContaModel categoriaConta = repository.getReferenceById(id);
			return new DetalheCategoriaContaRecord(categoriaConta);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
