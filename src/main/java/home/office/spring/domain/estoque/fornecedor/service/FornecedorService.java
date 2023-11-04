package home.office.spring.domain.estoque.fornecedor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.repository.FornecedorRepository;

@Service
public class FornecedorService {
	
	@Autowired
	private FornecedorRepository repository;
	
	@Transactional
	public FornecedorModel cadastrar(FornecedorRecord dados) {		
		var fornecedor = new FornecedorModel(dados);
		repository.save(fornecedor);		
		return fornecedor;
	}
	
	public Page<ListaFornecedorRecord> listar(@PageableDefault(page = 0, size = 5, sort = {"nome"}) Pageable paginacao) {
		return repository.findAllByAtivoTrue(paginacao).map(ListaFornecedorRecord::new);
	}
	
	@Transactional
	public DetalheFornecedorRecord atualizar(AtualizaFornecedorRecord dados) {
		FornecedorModel fornecedor = repository.getReferenceById(dados.id());
		fornecedor.atualizar(dados);
		return new DetalheFornecedorRecord(fornecedor);
	}
	
	@Transactional
	public void excluir(Long id) {
		repository.getReferenceById(id).inativar();
	}
	
	public DetalheFornecedorRecord detalhar(Long id) {
		FornecedorModel fornecedor = repository.getReferenceById(id);
		return new DetalheFornecedorRecord(fornecedor);
	}
	
}
