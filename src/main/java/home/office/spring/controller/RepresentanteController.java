package home.office.spring.controller;

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

import home.office.spring.domain.estoque.fornecedor.service.FornecedorService;
import home.office.spring.domain.estoque.representante.record.AtualizaRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.DetalheRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.ListaRepresentanteRecord;
import home.office.spring.domain.estoque.representante.record.RepresentanteRecord;
import home.office.spring.domain.estoque.representante.service.RepresentanteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/representante")
public class RepresentanteController {
	
	@Autowired
	private RepresentanteService service;
	
	@Autowired
	private FornecedorService fornecedorService;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid RepresentanteRecord dados, UriComponentsBuilder uriBuilder) {
		var fornecedor = fornecedorService.cadastrar(dados.fornecedor());
		var representante = service.cadastrar(dados, fornecedor);
		var uri = uriBuilder.path("/representante/{id}").buildAndExpand(representante.getId()).toUri();
		return ResponseEntity.created(uri).body(new DetalheRepresentanteRecord(representante));
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaRepresentanteRecord>> listar(Pageable paginacao){
		return ResponseEntity.ok(service.listar(paginacao));
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaRepresentanteRecord dados) {
		return ResponseEntity.ok(service.atualizar(dados));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheRepresentanteRecord> detalhar(@PathVariable Long id) {
		return ResponseEntity.ok(service.detalhar(id));	
	}

}
