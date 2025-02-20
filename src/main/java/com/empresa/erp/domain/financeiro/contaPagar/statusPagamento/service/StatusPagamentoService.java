package com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.AtualizaStatusPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.DetalheStatusPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.ListaStatusPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.record.StatusPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.repository.StatusPagamentoRepository;

@Service
public class StatusPagamentoService {
	
	@Autowired
	private StatusPagamentoRepository repository;
		
	@Transactional
	public StatusPagamentoModel cadastrar(StatusPagamentoRecord dados) {		
		try {			
			var statusPagamento = new StatusPagamentoModel(dados);
			repository.save(statusPagamento);		
			return statusPagamento;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaStatusPagamentoRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaStatusPagamentoRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaStatusPagamentoRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheStatusPagamentoRecord atualizar(AtualizaStatusPagamentoRecord dados) {
		try {
			StatusPagamentoModel statusPagamento = repository.getReferenceById(dados.id());
			statusPagamento.atualizar(dados);
			return new DetalheStatusPagamentoRecord(statusPagamento);
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
	
	public DetalheStatusPagamentoRecord detalhar(Long id) {
		try {
			StatusPagamentoModel statusPagamento = repository.getReferenceById(id);
			return new DetalheStatusPagamentoRecord(statusPagamento);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
