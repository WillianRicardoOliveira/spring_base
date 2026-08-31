package com.empresa.erp.domain.acesso.permissao.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.domain.acesso.permissao.record.DetalhePermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.ListaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.service.PermissaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/permissao")
@RequiredArgsConstructor
public class PermissaoController {

    private final PermissaoService service;

    @GetMapping
    @PreAuthorize("hasAuthority('ACESSO_PERMISSAO_LISTAR')")
    public ResponseEntity<Page<ListaPermissaoRecord>> listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable paginacao,
            String filtro
    ) {
        return ResponseEntity.ok(
                service.listar(paginacao, filtro)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_PERMISSAO_DETALHAR')")
    public ResponseEntity<DetalhePermissaoRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }
}