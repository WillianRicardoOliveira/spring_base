package com.empresa.erp.controller;

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

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.pessoa.model.PessoaModel;
import com.empresa.erp.domain.pessoa.record.AtualizaPessoaRecord;
import com.empresa.erp.domain.pessoa.record.DetalhePessoaRecord;
import com.empresa.erp.domain.pessoa.record.ListaPessoaRecord;
import com.empresa.erp.domain.pessoa.record.PessoaRecord;
import com.empresa.erp.domain.pessoa.service.PessoaService;

import jakarta.validation.Valid;

//@RestController
//@RequestMapping("/pessoa")
public class PessoaController {
	
	@Autowired
	private PessoaService service;
	
	@PostMapping("/cadastrar")
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid PessoaRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			PessoaModel pessoa = service.cadastrar(dados);
			var uri = uriBuilder.path("/pessoa/{id}").buildAndExpand(pessoa.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalhePessoaRecord(pessoa));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaPessoaRecord>> listar(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao){
		try {
			return ResponseEntity.ok(service.listar(paginacao));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaPessoaRecord dados) {
		try {
			return ResponseEntity.ok(service.atualizar(dados));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização.");
		}
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		try {
			service.excluir(id);
			return ResponseEntity.noContent().build();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão.");
		}
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalhePessoaRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}

}
