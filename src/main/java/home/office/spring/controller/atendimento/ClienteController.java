package home.office.spring.controller.atendimento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import home.office.spring.domain.atendimento.cliente.record.AtualizaClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.ClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.DetalheClienteRecord;
import home.office.spring.domain.atendimento.cliente.record.ListaClienteRecord;
import home.office.spring.domain.atendimento.cliente.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
public class ClienteController {
	
	@Autowired
	private ClienteService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid ClienteRecord dados, UriComponentsBuilder uriBuilder) {
		var cliente = service.cadastrar(dados);
		var uri = uriBuilder.path("/cliente/{id}").buildAndExpand(cliente.getId()).toUri();
		return ResponseEntity.created(uri).body(new DetalheClienteRecord(cliente));
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaClienteRecord>> listar(Pageable paginacao){
		return ResponseEntity.ok(service.listar(paginacao));
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaClienteRecord dados) {
		return ResponseEntity.ok(service.atualizar(dados));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheClienteRecord> detalhar(@PathVariable Long id) {
		return ResponseEntity.ok(service.detalhar(id));	
	}

}
