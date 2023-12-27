package home.office.spring.domain.estoque.produto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.produto.model.ProdutoModel;
import home.office.spring.domain.estoque.produto.record.AtualizaProdutoRecord;
import home.office.spring.domain.estoque.produto.record.DetalheProdutoRecord;
import home.office.spring.domain.estoque.produto.record.ListaProdutoRecord;
import home.office.spring.domain.estoque.produto.record.ProdutoRecord;
import home.office.spring.domain.estoque.produto.repository.ProdutoRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class ProdutoService {
	
	@Autowired
	private ProdutoRepository repository;
	
	@Transactional
	public ProdutoModel cadastrar(ProdutoRecord dados) {		
		try {
			var produto = new ProdutoModel(dados);
			repository.save(produto);		
			return produto;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaProdutoRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaProdutoRecord::new);
			} else {
				return repository.findAllByAtivoTrue(paginacao).map(ListaProdutoRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheProdutoRecord atualizar(AtualizaProdutoRecord dados) {
		try {
			ProdutoModel produto = repository.getReferenceById(dados.id());
			produto.atualizar(dados);
			return new DetalheProdutoRecord(produto);
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
	
	public DetalheProdutoRecord detalhar(Long id) {
		try {
			ProdutoModel produto = repository.getReferenceById(id);
			return new DetalheProdutoRecord(produto);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
