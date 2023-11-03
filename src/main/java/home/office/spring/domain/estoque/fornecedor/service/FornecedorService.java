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
	
}
