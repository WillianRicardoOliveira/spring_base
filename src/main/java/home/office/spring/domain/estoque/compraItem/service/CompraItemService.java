package home.office.spring.domain.estoque.compraItem.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.compra.model.CompraModel;
import home.office.spring.domain.estoque.compra.repository.CompraRepository;
import home.office.spring.domain.estoque.compraItem.model.CompraItemModel;
import home.office.spring.domain.estoque.compraItem.record.AtualizaCompraItemRecord;
import home.office.spring.domain.estoque.compraItem.record.CompraItemRecord;
import home.office.spring.domain.estoque.compraItem.record.DetalheCompraItemRecord;
import home.office.spring.domain.estoque.compraItem.record.ListaCompraItemRecord;
import home.office.spring.domain.estoque.compraItem.repository.CompraItemRepository;
import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.fornecedor.repository.FornecedorRepository;
import home.office.spring.domain.estoque.produto.model.ProdutoModel;
import home.office.spring.domain.estoque.produto.repository.ProdutoRepository;

@Service
public class CompraItemService {
	
	@Autowired
	private CompraItemRepository repository;
	
	@Autowired
	private CompraRepository compraRepository;
	
	@Autowired
	private FornecedorRepository fornecedorRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
		
	@Transactional
	public CompraItemModel cadastrar(CompraItemRecord dados) {
		CompraModel compra = compraRepository.getReferenceById(dados.compra());
		FornecedorModel fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor());
		ProdutoModel produto = produtoRepository.getReferenceById(dados.produto());
		
		BigDecimal quantidade = new BigDecimal(dados.quantidade());
	    BigDecimal total = quantidade.multiply(dados.valor());
        var controle = dados.quantidade();
		
		var compraItem = new CompraItemModel(dados, compra, fornecedor, produto, total, controle);
		repository.save(compraItem);		
		return compraItem;
	}
	
	public Page<ListaCompraItemRecord> listar(@PageableDefault(page = 0, size = 5, sort = {"nome"}) Pageable paginacao) {
		return repository.findAllByAtivoTrue(paginacao).map(ListaCompraItemRecord::new);
	}
	
	@Transactional
	public DetalheCompraItemRecord atualizar(AtualizaCompraItemRecord dados) {
		CompraItemModel compraItem = repository.getReferenceById(dados.id());
		var fornecedor = fornecedorRepository.getReferenceById(dados.fornecedor());
		var produto = produtoRepository.getReferenceById(dados.produto());
		BigDecimal quantidade = new BigDecimal(dados.quantidade());
	    BigDecimal total = quantidade.multiply(dados.valor());
        var controle = dados.quantidade();
		compraItem.atualizar(dados, fornecedor, produto, total, controle);
		return new DetalheCompraItemRecord(compraItem);
	}
	
	@Transactional
	public void excluir(Long id) {
		repository.getReferenceById(id).inativar();
	}
	
	public DetalheCompraItemRecord detalhar(Long id) {
		CompraItemModel compraItem = repository.getReferenceById(id);
		return new DetalheCompraItemRecord(compraItem);
	}
	
}
