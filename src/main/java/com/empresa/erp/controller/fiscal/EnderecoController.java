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
import com.empresa.erp.domain.fiscal.endereco.record.AtualizaEnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.record.DetalheEnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.record.EnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.record.ListaEnderecoRecord;
import com.empresa.erp.domain.fiscal.endereco.service.EnderecoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/endereco")
public class EnderecoController {
	
	@Autowired
	private EnderecoService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid EnderecoRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var endereco = service.cadastrar(dados);
			var uri = uriBuilder.path("/endereco/{id}").buildAndExpand(endereco.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheEnderecoRecord(endereco));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro do Endereço.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaEnderecoRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro){
		try {
			return ResponseEntity.ok(service.listar(paginacao, filtro));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem dos Endereços.");
		}
	}
	/*	
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaEnderecoRecord dados) {
		try {
			return ResponseEntity.ok(service.atualizar(dados));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do Endereço.");
		}
	}
	*/
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		try {
			service.excluir(id, true);
			return ResponseEntity.noContent().build();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do Endereço.");
		}
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheEnderecoRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do Endereço.");
		}
	}
	
	@GetMapping("/buscar/{cep}")
	public ResponseEntity<EnderecoRecord> buscaDadosEndereco(@PathVariable Long cep) throws Exception {
		try {
			return ResponseEntity.ok(service.buscaDadosEndereco(cep));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a busca do endereço.");
		}
	}

}
