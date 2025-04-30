package com.empresa.erp.modulos.fiscal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.modulos.fiscal.record.AtualizaEntidadeRecord;
import com.empresa.erp.modulos.fiscal.record.DetalheEntidadeRecord;
import com.empresa.erp.modulos.fiscal.record.EntidadeRecord;
import com.empresa.erp.modulos.fiscal.record.ListaEntidadeRecord;
import com.empresa.erp.modulos.fiscal.service.EntidadeService;
import com.empresa.erp.padrao.record.StatusRecord;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/entidade")
public class EntidadeController {
	
	@Autowired
	private EntidadeService service;
	
	@Operation(summary = "Cadastra uma nova entidade", description = "Cria uma nova entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@PostMapping
	public ResponseEntity<DetalheEntidadeRecord> cadastrar(@Valid @RequestBody EntidadeRecord request, UriComponentsBuilder uriBuilder) {		
		var entidade = service.cadastrar(request);			
		var uri = uriBuilder.path("/entidade/{id}").buildAndExpand(entidade.id()).toUri();			
		return ResponseEntity.created(uri).body(entidade);				
	}
		
	@Operation(summary = "Atualiza uma entidade", description = "Atualiza uma entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@PutMapping
	public ResponseEntity<DetalheEntidadeRecord> atualizar(@Valid @RequestBody AtualizaEntidadeRecord request) {					
		return ResponseEntity.ok(service.atualizar(request));
	}
			
	@Operation(summary = "Atualiza o status de uma entidade", description = "Ativa, inativa ou remove logicamente uma entidade no sistema (Cliente - Fornecedor - Parceiro - Transportadora)")
	@PutMapping("/status/{id}")	
	public ResponseEntity<Void> status(@PathVariable Long id, @Valid @RequestBody StatusRecord request) {			
		service.status(id, request.status());
		return ResponseEntity.noContent().build();
	}	
	
	@Operation(summary = "Busca uma entidade", description = "Busca uma entidade no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@GetMapping("/{id}")
	public ResponseEntity<DetalheEntidadeRecord> detalhar(@Parameter(description = "ID da entidade") @PathVariable Long id) {			
	    return ResponseEntity.ok(service.detalhar(id));		
	}
	
	@Operation(summary = "Lista as entidades", description = "Lista as entidades no sistema ( Cliente - Fornecedor - Parceiro - Transportadora )")
	@GetMapping
	public ResponseEntity<Page<ListaEntidadeRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, @Parameter(description = "Filtro para pesquisa") @RequestParam(required = false) String filtro) {
		return ResponseEntity.ok(service.listar(paginacao, filtro));
	}

}
