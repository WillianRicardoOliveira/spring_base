package com.empresa.erp.controller.financeiro.contaPagar.categoriaConta;

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
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.AtualizaCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.CategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.DetalheCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.record.ListaCategoriaContaRecord;
import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.service.CategoriaContaService;

import jakarta.validation.Valid;

//@RestController
//@RequestMapping("/categoriaConta")
public class CategoriaContaController {
	
	@Autowired
	private CategoriaContaService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid CategoriaContaRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var categoriaConta = service.cadastrar(dados);
			var uri = uriBuilder.path("/categoriaConta/{id}").buildAndExpand(categoriaConta.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheCategoriaContaRecord(categoriaConta));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaCategoriaContaRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro){
		try {
			return ResponseEntity.ok(service.listar(paginacao, filtro));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaCategoriaContaRecord dados) {
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
	public ResponseEntity<DetalheCategoriaContaRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}

}
