package com.empresa.erp.domain.configuracao.empresa.controller;

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

import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.DetalheEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.service.EmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/configuracao/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_EMPRESA_CRIAR')"
    )
    public ResponseEntity<DetalheEmpresaRecord> cadastrar(
            @RequestBody @Valid EmpresaRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        var empresa = service.cadastrar(dados);

        var uri = uriBuilder
                .path("/configuracao/empresa/{id}")
                .buildAndExpand(empresa.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(new DetalheEmpresaRecord(empresa));
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_EMPRESA_LISTAR')"
    )
    public ResponseEntity<Page<ListaEmpresaRecord>> listar(
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
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_EMPRESA_DETALHAR')"
    )
    public ResponseEntity<DetalheEmpresaRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @PutMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_EMPRESA_EDITAR')"
    )
    public ResponseEntity<DetalheEmpresaRecord> atualizar(
            @RequestBody @Valid AtualizaEmpresaRecord dados
    ) {
        return ResponseEntity.ok(
                service.atualizar(dados)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_EMPRESA_EXCLUIR')"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}