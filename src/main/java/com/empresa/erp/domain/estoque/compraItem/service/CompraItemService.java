package com.empresa.erp.domain.estoque.compraItem.service;
/*
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.domain.estoque.compra.model.CompraModel;
import com.empresa.erp.domain.estoque.compra.repository.CompraRepository;
import com.empresa.erp.domain.estoque.compraItem.model.CompraItemModel;
import com.empresa.erp.domain.estoque.compraItem.record.AtualizaCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.CompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.DetalheCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.record.ListaCompraItemRecord;
import com.empresa.erp.domain.estoque.compraItem.repository.CompraItemRepository;
import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;
import com.empresa.erp.domain.estoque.produto.repository.ProdutoRepository;
import com.empresa.erp.domain.fiscal.fornecedor.model.FornecedorModel;
import com.empresa.erp.domain.fiscal.fornecedor.repository.FornecedorRepository;
import com.empresa.erp.core.exception.ValidacaoException;

@Service
public class CompraItemService {
	
	@Autowired
	private CompraItemRepository repository;
	
	@Autowired
	private CompraRepository compraRepository;
	
	@Autowired
	private EntidadeRepository fornecedorRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
		
	@Transactional
	public CompraItemModel cadastrar(CompraItemRecord dados) {
		try {
			CompraModel compra = compraRepository.getReferenceById(dados.compra());
			FornecedorModel fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor().id());
			ProdutoModel produto = produtoRepository.getReferenceById(dados.produto().id());			
			BigDecimal quantidade = new BigDecimal(dados.quantidade());	        
	        BigDecimal nValor = dados.valor().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
			BigDecimal total = quantidade.multiply(nValor);
		    var controle = dados.quantidade();
			var compraItem = new CompraItemModel(dados, compra, fornecedor, produto, total, controle);
			repository.save(compraItem);		
			return compraItem;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaCompraItemRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByProdutoNomeContaining(paginacao, filtro).map(ListaCompraItemRecord::new);
			} else {
				return repository.findAllByAtivoTrue(paginacao).map(ListaCompraItemRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheCompraItemRecord atualizar(AtualizaCompraItemRecord dados) {
		try {
			CompraItemModel compraItem = repository.getReferenceById(dados.id());
			var fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor().id());
			var produto = produtoRepository.getReferenceById(dados.produto().id());
			BigDecimal quantidade = new BigDecimal(dados.quantidade());
			BigDecimal nValor = dados.valor().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
		    BigDecimal total = quantidade.multiply(nValor);
	        var controle = dados.quantidade();
			compraItem.atualizar(dados, fornecedor, produto, total, controle);
			return new DetalheCompraItemRecord(compraItem);
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
	
	public DetalheCompraItemRecord detalhar(Long id) {
		try {
			CompraItemModel compraItem = repository.getReferenceById(id);
			return new DetalheCompraItemRecord(compraItem);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
*/