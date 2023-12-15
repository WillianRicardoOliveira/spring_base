package home.office.spring.controller.estoque;

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

import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.AtualizaTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.DetalheTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.ListaTipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.record.TipoMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.tipoMovimentacao.service.TipoMovimentacaoService;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tipoMovimentacao")
public class TipoMovimentacaoController {
	
	@Autowired
	private TipoMovimentacaoService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid TipoMovimentacaoRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var tipoMovimentacao = service.cadastrar(dados);
			var uri = uriBuilder.path("/tipoMovimentacao/{id}").buildAndExpand(tipoMovimentacao.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheTipoMovimentacaoRecord(tipoMovimentacao));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaTipoMovimentacaoRecord>> listar(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao){
		try {
			return ResponseEntity.ok(service.listar(paginacao));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
		
	@PutMapping
	@Transactional
	public ResponseEntity atualizar(@RequestBody @Valid AtualizaTipoMovimentacaoRecord dados) {
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
	public ResponseEntity<DetalheTipoMovimentacaoRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}

}
