package com.empresa.erp.fiscal.entidade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.empresa.erp.fiscal.entidade.record.AtualizaEntidadeRecord;
import com.empresa.erp.fiscal.entidade.record.DetalheEntidadeRecord;
import com.empresa.erp.fiscal.entidade.record.EntidadeRecord;
import com.empresa.erp.fiscal.entidade.record.ListaEntidadeRecord;
import com.empresa.erp.fiscal.entidade.service.EntidadeService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/entidade")
public class EntidadeController {
	
	@Autowired
	private EntidadeService service;
	
	@Operation(summary = "Cadastra uma nova entidade", description = "Cria uma nova entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@PostMapping
	@Transactional
	public ResponseEntity<DetalheEntidadeRecord> cadastrar(@RequestBody @Valid EntidadeRecord dados, UriComponentsBuilder uriBuilder) {		
		var entidade = service.cadastrar(dados);			
		var uri = uriBuilder.path("/entidade/{id}").buildAndExpand(entidade.id()).toUri();			
		return ResponseEntity.created(uri).body(entidade);				
	}
	
	@Operation(summary = "Atualiza uma entidade", description = "Atualiza uma entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@PutMapping
	@Transactional
	public ResponseEntity<DetalheEntidadeRecord> atualizar(@RequestBody @Valid AtualizaEntidadeRecord dados) {					
		return ResponseEntity.ok(service.atualizar(dados));			
	}
	
	@Operation(summary = "Remove uma entidade", description = "Remove uma entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> remover(@PathVariable Long id) {			
		service.remover(id, true);			
		return ResponseEntity.noContent().build();		
	}	
	
	@Operation(summary = "Busca uma entidade", description = "Busca uma entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@GetMapping("/{id}")
	public ResponseEntity<DetalheEntidadeRecord> detalhar(@PathVariable Long id) {			
		return ResponseEntity.ok(service.detalhar(id));		
	}
	
	@Operation(summary = "Lista as entidades", description = "Lista as entidades no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@GetMapping 
	public ResponseEntity<Page<ListaEntidadeRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro) {		
		return ResponseEntity.ok(service.listar(paginacao, filtro));		
	}

}
