package home.office.spring.domain.estoque.movimentacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.compra.model.CompraModel;
import home.office.spring.domain.estoque.compra.repository.CompraRepository;
import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;
import home.office.spring.domain.estoque.movimentacao.record.DetalheMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.ListaMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.MovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.repository.MovimentacaoRepository;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.repository.TipoMovimentacaoRepository;
import home.office.spring.domain.estoque.produto.repository.ProdutoRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class MovimentacaoService {
	
	@Autowired
	private MovimentacaoRepository repository;
	
	@Autowired
	private CompraRepository compraRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private TipoMovimentacaoRepository tipoMovimentacaoRepository;

	@Transactional
	public MovimentacaoModel cadastrar(MovimentacaoRecord dados) {	
		try {
			CompraModel compra = null;
			var total = 0;					
			var produto = produtoRepository.getReferenceById(dados.produto().id());
			var tipoMovimentacao = tipoMovimentacaoRepository.getReferenceById(dados.tipoMovimentacao().id());
			if(dados.tipoMovimentacao().id().equals(1l)) { // INVENTARIO
				total = dados.quantidade();
			} else if(dados.tipoMovimentacao().id().equals(2l)) { // DANOS		
				if(produto.getQuantidade() >= dados.quantidade()) {
					total = produto.getQuantidade() - dados.quantidade();			
				} else {
					throw new ValidacaoException("O produto não possuí quantidade disponível em estoque.");
				}
			} else if(dados.tipoMovimentacao().id().equals(3l)) { // DEVOLUCAO
				total = produto.getQuantidade() + dados.quantidade();
			} else if(dados.tipoMovimentacao().id().equals(4l)) { // ENTRADA
				total = produto.getQuantidade() + dados.quantidade();				
				if(dados.compra() != null) {
					compra = compraRepository.getReferenceById(dados.compra());
					if (compra != null) {
						throw new ValidacaoException("A compra não foi identificada.");	
					}
				}
			} else if(dados.tipoMovimentacao().id().equals(5l)) { // SAIDA
				if(produto.getQuantidade() >= dados.quantidade()) {
					total = produto.getQuantidade() - dados.quantidade();			
				} else {
					throw new ValidacaoException("O produto não possuí quantidade disponível em estoque.");
				}
			} else {
				throw new ValidacaoException("O tipo da movimentação não foi identificado.");
			}	
			produto.setQuantidade(total);
			produtoRepository.save(produto);
			var movimentacao = new MovimentacaoModel(dados, compra, produto, total, tipoMovimentacao);
			repository.save(movimentacao);		
			return movimentacao;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaMovimentacaoRecord> listar(Pageable paginacao) {
		try {
			return repository.findAllByAtivoTrue(paginacao).map(ListaMovimentacaoRecord::new);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	public DetalheMovimentacaoRecord detalhar(Long id) {
		try {
			MovimentacaoModel movimentacao = repository.getReferenceById(id);
			return new DetalheMovimentacaoRecord(movimentacao);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
