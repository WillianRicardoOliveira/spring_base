package home.office.spring.domain.estoque.movimentacao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.atendimento.cliente.model.ClienteModel;
import home.office.spring.domain.atendimento.cliente.repository.ClienteRepository;
import home.office.spring.domain.estoque.compra.model.CompraModel;
import home.office.spring.domain.estoque.compra.repository.CompraRepository;
import home.office.spring.domain.estoque.movimentacao.constante.TipoMovimentacao;
import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;
import home.office.spring.domain.estoque.movimentacao.record.DetalheMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.ListaMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.MovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.repository.MovimentacaoRepository;
import home.office.spring.domain.estoque.produto.repository.ProdutoRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class MovimentacaoService {
	
	@Autowired
	private MovimentacaoRepository repository;
	
	@Autowired
	private CompraRepository compraRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Transactional
	public MovimentacaoModel cadastrar(MovimentacaoRecord dados) {	
		try {
			CompraModel compra = null;
			ClienteModel cliente = null;	
			var total = 0;		
			var produto = produtoRepository.getReferenceById(dados.produto());
			if(dados.tipoMovimentacao().equals(TipoMovimentacao.INVENTARIO)) {
				total = dados.quantidade();
			} else if(dados.tipoMovimentacao().equals(TipoMovimentacao.DANOS)) {			
				if(produto.getQuantidade() >= dados.quantidade()) {
					total = produto.getQuantidade() - dados.quantidade();			
				} else {
					throw new ValidacaoException("O produto não possuí quantidade disponível em estoque.");
				}
			} else if(dados.tipoMovimentacao().equals(TipoMovimentacao.DEVOLUCAO)) {
				total = produto.getQuantidade() + dados.quantidade();
			} else if(dados.tipoMovimentacao().equals(TipoMovimentacao.ENTRADA)) {
				if(dados.compra() != null) {
					compra = compraRepository.getReferenceById(dados.compra());
					total = produto.getQuantidade() + dados.quantidade();
				} else {
					throw new ValidacaoException("A compra não foi identificada.");	
				}
			} else if(dados.tipoMovimentacao().equals(TipoMovimentacao.SAIDA)) {
				if(dados.cliente() != null) {
					cliente = clienteRepository.getReferenceById(dados.cliente());
				}
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
			var movimentacao = new MovimentacaoModel(dados, compra, cliente, produto, total);
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
