package home.office.spring.controller.estoque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import home.office.spring.domain.estoque.movimentacao.record.DetalheMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.ListaMovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.record.MovimentacaoRecord;
import home.office.spring.domain.estoque.movimentacao.service.MovimentacaoService;
import home.office.spring.infra.exception.ValidacaoException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimentacao")
public class MovimentacaoController {
	
	@Autowired
	private MovimentacaoService service;
	
	@PostMapping
	@Transactional
	public ResponseEntity cadastrar(@RequestBody @Valid MovimentacaoRecord dados, UriComponentsBuilder uriBuilder) {
		try {
			var movimentacao = service.cadastrar(dados);
			var uri = uriBuilder.path("/movimentacao/{id}").buildAndExpand(movimentacao.getId()).toUri();
			return ResponseEntity.created(uri).body(new DetalheMovimentacaoRecord(movimentacao));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o cadastro.");
		}
	}	
	
	@GetMapping
	public ResponseEntity<Page<ListaMovimentacaoRecord>> listar(@PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao){
		try {
			return ResponseEntity.ok(service.listar(paginacao));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar a listagem.");
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalheMovimentacaoRecord> detalhar(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(service.detalhar(id));
		} catch (ValidacaoException e) {
			throw new ValidacaoException("Não foi possível realizar o detalhamento.");
		}
	}

}
