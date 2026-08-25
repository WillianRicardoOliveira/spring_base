package com.empresa.erp.controller.acesso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.DetalhePerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.ListaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.service.PerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

	private final PerfilService service;

	@PostMapping
	@PreAuthorize("hasAuthority('ACESSO_PERFIL_CRIAR')")
	public ResponseEntity<DetalhePerfilRecord> cadastrar(@RequestBody @Valid PerfilRecord dados, UriComponentsBuilder uriBuilder) {
	    var perfil = service.cadastrar(dados);
	    var uri = uriBuilder.path("/perfil/{id}").buildAndExpand(perfil.getId()).toUri();
	    return ResponseEntity.created(uri).body(new DetalhePerfilRecord(perfil));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('ACESSO_PERFIL_LISTAR')")
	public ResponseEntity<Page<ListaPerfilRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro) {
	    return ResponseEntity.ok(service.listar(paginacao, filtro));
	}

	@PutMapping
	@PreAuthorize("hasAuthority('ACESSO_PERFIL_EDITAR')")
	public ResponseEntity<DetalhePerfilRecord> atualizar(@RequestBody @Valid AtualizaPerfilRecord dados) {
	    return ResponseEntity.ok(service.atualizar(dados));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ACESSO_PERFIL_EXCLUIR')")
	public ResponseEntity<Void> excluir(@PathVariable Long id) {
	    service.excluir(id);
	    return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ACESSO_PERFIL_DETALHAR')")
	public ResponseEntity<DetalhePerfilRecord> detalhar(@PathVariable Long id) {
	    return ResponseEntity.ok(service.detalhar(id));
	}
    
}