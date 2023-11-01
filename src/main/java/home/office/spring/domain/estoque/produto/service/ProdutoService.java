package home.office.spring.domain.estoque.produto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.produto.model.ProdutoModel;
import home.office.spring.domain.estoque.produto.record.AtualizaProdutoRecord;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;
import home.office.spring.domain.estoque.produto.record.ListaProdutoRecord;
import home.office.spring.domain.estoque.produto.record.ProdutoRecord;
import home.office.spring.domain.estoque.produto.repository.ProdutoRepository;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository repository;
	
	@Transactional
	public ProdutoModel cadastrar(ProdutoRecord dados) {		
		var produto = new ProdutoModel(dados);
		repository.save(produto);		
		return produto;
	}
	
	public Page<ListaProdutoRecord> listar(@PageableDefault(page = 0, size = 5, sort = {"nome"}) Pageable paginacao) {
		return repository.findAllByAtivoTrue(paginacao).map(ListaProdutoRecord::new);
	}
	
	@Transactional
	public DetalheProdutoRecord atualizar(AtualizaProdutoRecord dados) {
		ProdutoModel produto = repository.getReferenceById(dados.id());
		produto.atualizar(dados);
		return new DetalheProdutoRecord(produto);
	}
	
	@Transactional
	public void excluir(Long id) {
		repository.getReferenceById(id).inativar();
	}
	
	public DetalheProdutoRecord detalhar(Long id) {
		ProdutoModel produto = repository.getReferenceById(id);
		return new DetalheProdutoRecord(produto);
	}
	
}
