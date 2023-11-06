package home.office.spring.controller.estoque;

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

import home.office.spring.domain.estoque.compra.record.AtualizaCompraRecord;
import home.office.spring.domain.estoque.compra.record.CompraRecord;
import home.office.spring.domain.estoque.compra.record.DetalheCompraRecord;
import home.office.spring.domain.estoque.compra.record.ListaCompraRecord;
import home.office.spring.domain.estoque.compra.service.CompraService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/compra")
public class CompraController {
	
	@Autowired
	private CompraService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid CompraRecord dados, UriComponentsBuilder uriBuilder) {
		var compra = service.cadastrar(dados);
		var uri = uriBuilder.path("/compra/{id}").buildAndExpand(compra.getId()).toUri();
		return ResponseEntity.created(uri).body(new DetalheCompraRecord(compra));
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaCompraRecord>> listar(Pageable paginacao){
		return ResponseEntity.ok(service.listar(paginacao));
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaCompraRecord dados) {
		return ResponseEntity.ok(service.atualizar(dados));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheCompraRecord> detalhar(@PathVariable Long id) {
		return ResponseEntity.ok(service.detalhar(id));	
	}

}
