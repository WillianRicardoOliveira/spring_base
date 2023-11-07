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

import home.office.spring.domain.estoque.compraItem.record.AtualizaCompraItemRecord;
import home.office.spring.domain.estoque.compraItem.record.CompraItemRecord;
import home.office.spring.domain.estoque.compraItem.record.DetalheCompraItemRecord;
import home.office.spring.domain.estoque.compraItem.record.ListaCompraItemRecord;
import home.office.spring.domain.estoque.compraItem.service.CompraItemService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/compraItem")
public class CompraItemController {
	
	@Autowired
	private CompraItemService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid CompraItemRecord dados, UriComponentsBuilder uriBuilder) {
		var compraItem = service.cadastrar(dados);
		var uri = uriBuilder.path("/compraItem/{id}").buildAndExpand(compraItem.getId()).toUri();
		return ResponseEntity.created(uri).body(new DetalheCompraItemRecord(compraItem));
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaCompraItemRecord>> listar(Pageable paginacao){
		return ResponseEntity.ok(service.listar(paginacao));
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaCompraItemRecord dados) {
		return ResponseEntity.ok(service.atualizar(dados));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheCompraItemRecord> detalhar(@PathVariable Long id) {
		return ResponseEntity.ok(service.detalhar(id));	
	}

}
