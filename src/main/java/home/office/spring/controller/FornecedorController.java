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

import home.office.spring.domain.estoque.fornecedor.model.FornecedorModel;
import home.office.spring.domain.estoque.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.estoque.fornecedor.service.FornecedorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/fornecedor")
public class FornecedorController {
	
	@Autowired
	private FornecedorService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid FornecedorRecord dados, UriComponentsBuilder uriBuilder) {
		FornecedorModel fornecedor = service.cadastrar(dados);
		var uri = uriBuilder.path("/fornecedor/{id}").buildAndExpand(fornecedor.getId()).toUri();
		return ResponseEntity.created(uri).body(new DetalheFornecedorRecord(fornecedor));
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaFornecedorRecord>> listar(Pageable paginacao){
		return ResponseEntity.ok(service.listar(paginacao));
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaFornecedorRecord dados) {
		return ResponseEntity.ok(service.atualizar(dados));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		service.excluir(id);
		return ResponseEntity.noContent().build();
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheFornecedorRecord> detalhar(@PathVariable Long id) {
		return ResponseEntity.ok(service.detalhar(id));	
	}

}
