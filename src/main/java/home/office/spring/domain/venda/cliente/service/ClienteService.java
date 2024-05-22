package home.office.spring.domain.venda.cliente.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import home.office.spring.domain.venda.cliente.model.ClienteModel;
import home.office.spring.domain.venda.cliente.record.AtualizaClienteRecord;
import home.office.spring.domain.venda.cliente.record.ClienteRecord;
import home.office.spring.domain.venda.cliente.record.DetalheClienteRecord;
import home.office.spring.domain.venda.cliente.record.ListaClienteRecord;
import home.office.spring.domain.venda.cliente.repository.ClienteRepository;
import home.office.spring.infra.exception.ValidacaoException;

@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository repository;
	
	@Transactional
	public ClienteModel cadastrar(ClienteRecord dados) {		
		try {
			var cliente = new ClienteModel(dados, null);
			repository.save(cliente);		
			return cliente;
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}
	
	public Page<ListaClienteRecord> listar(Pageable paginacao) {
		try {
			return repository.findAllByAtivoTrue(paginacao).map(ListaClienteRecord::new);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@Transactional
	public DetalheClienteRecord atualizar(AtualizaClienteRecord dados) {
		try {
			ClienteModel cliente = repository.getReferenceById(dados.id());
			cliente.atualizar(dados, null);
			return new DetalheClienteRecord(cliente);
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
	
	public DetalheClienteRecord detalhar(Long id) {
		try {
			ClienteModel cliente = repository.getReferenceById(id);
			return new DetalheClienteRecord(cliente);
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}
	
}
