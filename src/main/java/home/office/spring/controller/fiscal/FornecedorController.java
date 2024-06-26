package home.office.spring.controller.fiscal;

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

import home.office.spring.domain.fiscal.fornecedor.record.AtualizaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.DetalheFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.FornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.record.ListaFornecedorRecord;
import home.office.spring.domain.fiscal.fornecedor.service.FornecedorService;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/fornecedor")
public class FornecedorController {
	
	@Autowired
	private FornecedorService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid FornecedorRecord dados, UriComponentsBuilder uriBuilder) {
		
		try {
			
			var fornecedor = service.cadastrar(dados);
			
			var uri = uriBuilder.path("/fornecedor/{id}").buildAndExpand(fornecedor.getId()).toUri();
			
			return ResponseEntity.created(uri).body(new DetalheFornecedorRecord(fornecedor));
			
		} catch (ValidacaoException e) {
			
			throw new ValidacaoException("Não foi possível realizar o cadastro do fornecedor.");
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping 
	public ResponseEntity<Page<ListaFornecedorRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro){
		try {
			return ResponseEntity.ok(service.listar(paginacao, filtro));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem do fornecedor.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaFornecedorRecord dados) {
		try {
			return ResponseEntity.ok(service.atualizar(dados));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a atualização do fornecedor.");
		}
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity excluir(@PathVariable Long id) {
		try {
			service.excluir(id, true);
			return ResponseEntity.noContent().build();
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a exclusão do fornecedor.");
		}
	}	
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheFornecedorRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento do fornecedor.");
		}
	}

}
