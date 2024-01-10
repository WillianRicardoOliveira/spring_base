package home.office.spring.controller.financeiro.contaPagar.statusPagamento;

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

import home.office.spring.domain.financeiro.contaPagar.statusPagamento.record.AtualizaStatusPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.record.DetalheStatusPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.record.ListaStatusPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.record.StatusPagamentoRecord;
import home.office.spring.domain.financeiro.contaPagar.statusPagamento.service.StatusPagamentoService;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/statusPagamento")
public class StatusPagamentoController {
	
	@Autowired
	private StatusPagamentoService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid StatusPagamentoRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var statusPagamento = service.cadastrar(dados);
			var uri = uriBuilder.path("/statusPagamento/{id}").buildAndExpand(statusPagamento.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheStatusPagamentoRecord(statusPagamento));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaStatusPagamentoRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro){
		try {
			return ResponseEntity.ok(service.listar(paginacao, filtro));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaStatusPagamentoRecord dados) {
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
	public ResponseEntity<DetalheStatusPagamentoRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}

}
