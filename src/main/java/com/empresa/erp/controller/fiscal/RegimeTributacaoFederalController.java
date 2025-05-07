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
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.AtualizaRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.DetalheRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.ListaRegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.record.RegimeTributacaoFederalRecord;
import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.service.RegimeTributacaoFederalService;

import jakarta.validation.Valid;

//@RestController
//@RequestMapping("/regimeTributacaoFederal")
public class RegimeTributacaoFederalController {
	
	@Autowired
	private RegimeTributacaoFederalService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid RegimeTributacaoFederalRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var regimeTributacaoFederal = service.cadastrar(dados);
			var uri = uriBuilder.path("/regimeTributacaoFederal/{id}").buildAndExpand(regimeTributacaoFederal.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheRegimeTributacaoFederalRecord(regimeTributacaoFederal));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do Regime de Tributação Federal.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaRegimeTributacaoFederalRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro){
		try {
			return ResponseEntity.ok(service.listar(paginacao, filtro));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos Regimes de Tributação Federal.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaRegimeTributacaoFederalRecord dados) {
		try {
			return ResponseEntity.ok(service.atualizar(dados));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do Regime de Tributação Federal.");
		}
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		try {
			service.excluir(id);
			return ResponseEntity.noContent().build();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do Regime de Tributação Federal.");
		}
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheRegimeTributacaoFederalRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do Regime de Tributação Federal.");
		}
	}

}
