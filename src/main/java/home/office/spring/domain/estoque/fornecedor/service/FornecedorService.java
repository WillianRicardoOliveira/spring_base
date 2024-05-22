package home.office.spring.domain.estoque.fornecedor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.repository.FornecedorRepository;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.transaction.Transactional;

@Service
public class FornecedorService {
	
	@Autowired
	private FornecedorRepository repository;
	
	@Transactional
	public FornecedorModel cadastrar(FornecedorRecord dados) {		
		try {
			var fornecedor = new FornecedorModel(dados);
			repository.save(fornecedor);		
			return fornecedor;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do fornecedor.");
		}
	}
	
	public Page<ListaFornecedorRecord> listar(Pageable paginacao, String filtro) {
		try {
			if(filtro != null) {
				return repository.findByNomeContaining(paginacao, filtro).map(ListaFornecedorRecord::new);
			} else {			
				return repository.findAllByAtivoTrue(paginacao).map(ListaFornecedorRecord::new);
			}
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos fornecedores.");
		}
	}
	
	@Transactional
	public DetalheFornecedorRecord atualizar(AtualizaFornecedorRecord dados) {
		try {
			FornecedorModel fornecedor = repository.getReferenceById(dados.id());
			fornecedor.atualizar(dados);
			return new DetalheFornecedorRecord(fornecedor);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do fornecedor.");
		}
	}
	
	@Transactional
	public void excluir(Long id) {
		try {
			repository.getReferenceById(id).inativar();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do fornecedor.");
		}
	}
	
	public DetalheFornecedorRecord detalhar(Long id) {
		try {
			FornecedorModel fornecedor = repository.getReferenceById(id);
			return new DetalheFornecedorRecord(fornecedor);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do fornecedor.");
		}
	}
	
}
