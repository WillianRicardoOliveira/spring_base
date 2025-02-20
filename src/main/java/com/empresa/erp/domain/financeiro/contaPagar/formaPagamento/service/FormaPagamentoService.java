package com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.AtualizaFormaPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.DetalheFormaPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.FormaPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.record.ListaFormaPagamentoRecord;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.repository.FormaPagamentoRepository;

@Service
public class FormaPagamentoService {
	
	@Autowired
	private FormaPagamentoRepository repository;
		
	@Transactional
	public FormaPagamentoModel cadastrar(FormaPagamentoRecord dados) {		
		try {			
			var formaPagamento = new FormaPagamentoModel(dados);
			repository.save(formaPagamento);		
			return formaPagamento;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaFormaPagamentoRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaFormaPagamentoRecord::new);
			} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaFormaPagamentoRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheFormaPagamentoRecord atualizar(AtualizaFormaPagamentoRecord dados) {
		try {
			FormaPagamentoModel formaPagamento = repository.getReferenceById(dados.id());
			formaPagamento.atualizar(dados);
			return new DetalheFormaPagamentoRecord(formaPagamento);
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
	
	public DetalheFormaPagamentoRecord detalhar(Long id) {
		try {
			FormaPagamentoModel formaPagamento = repository.getReferenceById(id);
			return new DetalheFormaPagamentoRecord(formaPagamento);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
