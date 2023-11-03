package home.office.spring.domain.estoque.representante.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.representante.model.RepresentanteModel;
import home.office.spring.domain.estoque.representante.record.AtualizaRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.DetalheRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.ListaRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.RepresentanteRecord;
import home.office.spring.domain.estoque.representante.repository.RepresentanteRepository;

@Service
public class RepresentanteService {
	
	@Autowired
	private RepresentanteRepository repository;
	
	@Transactional
	public RepresentanteModel cadastrar(RepresentanteRecord dados, FornecedorModel fornecedor) {		
		var representante = new RepresentanteModel(dados, fornecedor);
		repository.save(representante);			
		return representante;
	}
	
	public Page<ListaRepresentanteRecord> listar(@PageableDefault(page = 0, size = 5, sort = {"nome"}) Pageable paginacao) {
		return repository.findAllByAtivoTrue(paginacao).map(ListaRepresentanteRecord::new);
	}
	
	@Transactional
	public DetalheRepresentanteRecord atualizar(AtualizaRepresentanteRecord dados) {
		RepresentanteModel representante = repository.getReferenceById(dados.id());
		representante.atualizar(dados);
		return new DetalheRepresentanteRecord(representante);
	}
	
	@Transactional
	public void excluir(Long id) {
		repository.getReferenceById(id).inativar();
	}
	
	public DetalheRepresentanteRecord detalhar(Long id) {
		RepresentanteModel representante = repository.getReferenceById(id);
		return new DetalheRepresentanteRecord(representante);
	}

}
