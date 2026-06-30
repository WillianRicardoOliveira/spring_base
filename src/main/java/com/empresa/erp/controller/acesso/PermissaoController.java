package com.empresa.erp.controller.acesso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.DetalhePermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.ListaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.service.PermissaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/permissao")
@RequiredArgsConstructor
public class PermissaoController {

    private final PermissaoService service;

    @PostMapping
    public ResponseEntity<DetalhePermissaoRecord> cadastrar(@RequestBody @Valid PermissaoRecord dados, UriComponentsBuilder uriBuilder) {
        PermissaoModel permissao = service.cadastrar(dados);
        var uri = uriBuilder.path("/permissao/{id}").buildAndExpand(permissao.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalhePermissaoRecord(permissao));
    }

    @GetMapping
    public ResponseEntity<Page<ListaPermissaoRecord>> listar(@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable paginacao, String filtro) {
        return ResponseEntity.ok(service.listar(paginacao, filtro));
    }

    @PutMapping
    public ResponseEntity<DetalhePermissaoRecord> atualizar(@RequestBody @Valid AtualizaPermissaoRecord dados) {
        return ResponseEntity.ok(service.atualizar(dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhePermissaoRecord> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalhar(id));
    }
    
}