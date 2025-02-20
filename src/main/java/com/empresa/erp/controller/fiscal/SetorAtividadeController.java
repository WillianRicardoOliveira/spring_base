package com.empresa.erp.controller.fiscal;

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
import com.empresa.erp.domain.fiscal.setorAtividade.record.AtualizaSetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.DetalheSetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.ListaSetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.record.SetorAtividadeRecord;
import com.empresa.erp.domain.fiscal.setorAtividade.service.SetorAtividadeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/setorAtividade")
public class SetorAtividadeController {
	
	@Autowired
	private SetorAtividadeService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid SetorAtividadeRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var setorAtividade = service.cadastrar(dados);
			var uri = uriBuilder.path("/setorAtividade/{id}").buildAndExpand(setorAtividade.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheSetorAtividadeRecord(setorAtividade));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do Setor de Atividade.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaSetorAtividadeRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro){
		try {
			return ResponseEntity.ok(service.listar(paginacao, filtro));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos Setores de Atividade.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaSetorAtividadeRecord dados) {
		try {
			return ResponseEntity.ok(service.atualizar(dados));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do Setor de Atividade.");
		}
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		try {
			service.excluir(id);
			return ResponseEntity.noContent().build();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do Setor de Atividade.");
		}
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheSetorAtividadeRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do Setor de Atividade.");
		}
	}

}
