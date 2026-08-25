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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.DetalhePerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.ListaPerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.service.PerfilPermissaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/perfil-permissao")
@RequiredArgsConstructor
public class PerfilPermissaoController {

    private final PerfilPermissaoService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ACESSO_PERFIL_PERMISSAO_CRIAR')")
    public ResponseEntity<DetalhePerfilPermissaoRecord> cadastrar(@RequestBody @Valid PerfilPermissaoRecord dados, UriComponentsBuilder uriBuilder) {
        PerfilPermissaoModel perfilPermissao = service.cadastrar(dados);
        var uri = uriBuilder.path("/perfil-permissao/{id}").buildAndExpand(perfilPermissao.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalhePerfilPermissaoRecord(perfilPermissao));
    }

    @GetMapping("/perfil/{idPerfil}")
    @PreAuthorize("hasAuthority('ACESSO_PERFIL_PERMISSAO_LISTAR')")
    public ResponseEntity<Page<ListaPerfilPermissaoRecord>> listarPorPerfil(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, @PathVariable Long idPerfil) {
        return ResponseEntity.ok(service.listarPorPerfil(paginacao, idPerfil));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_PERFIL_PERMISSAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_PERFIL_PERMISSAO_DETALHAR')")
    public ResponseEntity<DetalhePerfilPermissaoRecord> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalhar(id));
    }
    
}