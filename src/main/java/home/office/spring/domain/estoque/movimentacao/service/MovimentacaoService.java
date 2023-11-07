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
		CompraModel compra = null;
		ClienteModel cliente = null;	
		var total = 0;		
		var produto = produtoRepository.getReferenceById(dados.produto());
		if(dados.tipoMovimentacao().equals(TipoMovimentacao.INVENTARIO)) {
			total = dados.quantidade();
		}
		if(dados.tipoMovimentacao().equals("DANOS")) {			
			if(produto.getQuantidade() >= dados.quantidade()) {
				total = produto.getQuantidade() - dados.quantidade();			
			} else {
				throw new ValidacaoException("O produto não possuí quantidade disponível em estoque.");
			}
		}
		if(dados.tipoMovimentacao().equals("DEVOLUCAO")) {
			total = produto.getQuantidade() + dados.quantidade();
		}
		if(dados.tipoMovimentacao().equals("ENTRADA")) {
			if(dados.compra() != null) {
				compra = compraRepository.getReferenceById(dados.compra());
				total = produto.getQuantidade() + dados.quantidade();
			} else {
				throw new ValidacaoException("A compra não foi identificada.");	
			}
		} else if(dados.tipoMovimentacao().equals("SAIDA")) {
			if(dados.cliente() != null) {
				cliente = clienteRepository.getReferenceById(dados.cliente());
			}
			if(produto.getQuantidade() >= dados.quantidade()) {
				total = produto.getQuantidade() - dados.quantidade();			
			} else {
				throw new ValidacaoException("O produto não possuí quantidade disponível em estoque.");
			}
		}		
		produto.setQuantidade(total);
		produtoRepository.save(produto);
		var movimentacao = new MovimentacaoModel(dados, compra, cliente, produto, total);
		repository.save(movimentacao);		
		return movimentacao;
	}
	
	public Page<ListaMovimentacaoRecord> listar(@PageableDefault(page = 0, size = 5, sort = {"id"}) Pageable paginacao) {
		return repository.findAllByAtivoTrue(paginacao).map(ListaMovimentacaoRecord::new);
	}
	
	public DetalheMovimentacaoRecord detalhar(Long id) {
		MovimentacaoModel movimentacao = repository.getReferenceById(id);
		return new DetalheMovimentacaoRecord(movimentacao);
	}
	
}
