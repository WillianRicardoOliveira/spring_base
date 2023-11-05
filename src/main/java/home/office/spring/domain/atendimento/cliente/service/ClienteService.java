package home.office.spring.domain.atendimento.cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.atendimento.cliente.model.ClienteModel;
import home.office.spring.domain.atendimento.cliente.record.AtualizaClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.ClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.DetalheClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.ListaClienteRecord;
import home.office.spring.domain.atendimento.cliente.repository.ClienteRepository;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	
	@Transactional
	public ClienteModel cadastrar(ClienteRecord dados) {		
		var cliente = new ClienteModel(dados);
		repository.save(cliente);		
		return cliente;
	}
	
	public Page<ListaClienteRecord> listar(@PageableDefault(page = 0, size = 5, sort = {"nome"}) Pageable paginacao) {
		return repository.findAllByAtivoTrue(paginacao).map(ListaClienteRecord::new);
	}
	
	@Transactional
	public DetalheClienteRecord atualizar(AtualizaClienteRecord dados) {
		ClienteModel cliente = repository.getReferenceById(dados.id());
		cliente.atualizar(dados);
		return new DetalheClienteRecord(cliente);
	}
	
	@Transactional
	public void excluir(Long id) {
		repository.getReferenceById(id).inativar();
	}
	
	public DetalheClienteRecord detalhar(Long id) {
		ClienteModel cliente = repository.getReferenceById(id);
		return new DetalheClienteRecord(cliente);
	}
	
}
