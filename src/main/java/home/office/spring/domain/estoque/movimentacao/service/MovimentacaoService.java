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
import home.office.spring.domain.estoque.movimentacao.model.MovimentacaoModel;
import home.office.spring.domain.estoque.movimentacao.record.DetalheMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.ListaMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.MovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.repository.MovimentacaoRepository;
import home.office.spring.domain.estoque.produto.repository.ProdutoRepository;

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
		CompraModel nCompra = null;
		ClienteModel nCliente = null;		
		if(dados.tipoMovimentacao().equals("ENTRADA")) {
			if(dados.compra() != null) {
				nCompra = compraRepository.getReferenceById(dados.compra());
			} else {
			 // VALIDAR 	
			}
		} else if(dados.tipoMovimentacao().equals("SAIDA")) {
			if(dados.cliente() != null) {
				nCliente = clienteRepository.getReferenceById(dados.cliente());
			}
		}
		
		// calcular o total
		// salvar o total no produto
		var nProduto = produtoRepository.getReferenceById(dados.produto());
		
		var movimentacao = new MovimentacaoModel(dados, nCompra, nCliente, nProduto);
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
