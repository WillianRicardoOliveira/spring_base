package com.empresa.erp.domain.financeiro.contaPagar.service;
/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.model.SubCategoriaContaModel;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.subCategoriaConta.repository.SubCategoriaContaRepository;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;
import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.repository.FormaPagamentoRepository;
import com.empresa.erp.domain.financeiro.contaPagar.model.ContaPagarModel;
import com.empresa.erp.domain.financeiro.contaPagar.record.AtualizaContaPagarRecord;
import com.empresa.erp.domain.financeiro.contaPagar.record.ContaPagarRecord;
import com.empresa.erp.domain.financeiro.contaPagar.record.DetalheContaPagarRecord;
import com.empresa.erp.domain.financeiro.contaPagar.record.ListaContaPagarRecord;
import com.empresa.erp.domain.financeiro.contaPagar.repository.ContaPagarRepository;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;
import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.repository.StatusPagamentoRepository;
import com.empresa.erp.domain.fiscal.fornecedor.model.FornecedorModel;
import com.empresa.erp.domain.fiscal.fornecedor.repository.FornecedorRepository;
import com.empresa.erp.core.exception.ValidacaoException;


@Service
public class ContaPagarService {
	
	@Autowired
	private ContaPagarRepository repository;
	
	@Autowired
	private EntidadeRepository fornecedorRepository;
	
	@Autowired
	private SubCategoriaContaRepository subCategoriaContaRepository;
	
	@Autowired
	private StatusPagamentoRepository statusPagamentoRepository;
	
	@Autowired
	private FormaPagamentoRepository formaPagamentoRepository;
	
	
	@Transactional
	public ContaPagarModel cadastrar(ContaPagarRecord dados) {		
		try {			
			FornecedorModel fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor().id());
			SubCategoriaContaModel subCategoriaConta = subCategoriaContaRepository.getReferenceById(dados.subCategoriaConta().id());			
			StatusPagamentoModel statusPagamento = statusPagamentoRepository.getReferenceById(dados.statusPagamento().id());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());			
			var contaPagar = new ContaPagarModel(dados, fornecedor, subCategoriaConta, statusPagamento, formaPagamento);
			repository.save(contaPagar);		
			return contaPagar;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaContaPagarRecord> listar(Pageable paginacao, String filtro) {
		try {
			//if(filtro != null) {
			//	return repository.findByFornecedorNomeContaining(paginacao, filtro).map(ListaContaPagarRecord::new);
			//} else {		
				return repository.findAllByAtivoTrue(paginacao).map(ListaContaPagarRecord::new);
			//}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheContaPagarRecord atualizar(AtualizaContaPagarRecord dados) {
		try {
			FornecedorModel fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor().id());
			SubCategoriaContaModel subCategoriaConta = subCategoriaContaRepository.getReferenceById(dados.subCategoriaConta().id());
			StatusPagamentoModel statusPagamento = statusPagamentoRepository.getReferenceById(dados.statusPagamento().id());			
			FormaPagamentoModel formaPagamento = formaPagamentoRepository.getReferenceById(dados.formaPagamento().id());
			ContaPagarModel contaPagar = repository.getReferenceById(dados.id());
			contaPagar.atualizar(dados, fornecedor, subCategoriaConta, statusPagamento, formaPagamento);
			return new DetalheContaPagarRecord(contaPagar);
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
	
	public DetalheContaPagarRecord detalhar(Long id) {
		try {
			ContaPagarModel contaPagar = repository.getReferenceById(id);
			return new DetalheContaPagarRecord(contaPagar);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
*/