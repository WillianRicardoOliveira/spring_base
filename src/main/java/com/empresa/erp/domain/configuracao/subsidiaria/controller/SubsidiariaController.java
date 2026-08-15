package com.empresa.erp.domain.configuracao.subsidiaria.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.record.AtualizaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.DetalheSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.ListaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.SubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.service.SubsidiariaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/configuracao/subsidiaria")
@RequiredArgsConstructor
public class SubsidiariaController {

    private final SubsidiariaService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_SUBSIDIARIA_CRIAR')"
    )
    public ResponseEntity<DetalheSubsidiariaRecord> cadastrar(
            @RequestBody @Valid SubsidiariaRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        SubsidiariaModel subsidiaria =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path("/configuracao/subsidiaria/{id}")
                .buildAndExpand(subsidiaria.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(
                        new DetalheSubsidiariaRecord(
                                subsidiaria
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_SUBSIDIARIA_LISTAR')"
    )
    public ResponseEntity<Page<ListaSubsidiariaRecord>> listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable paginacao,

            @RequestParam(required = false)
            Long idEmpresa,

            @RequestParam(required = false)
            String filtro
    ) {
        return ResponseEntity.ok(
                service.listar(
                        paginacao,
                        idEmpresa,
                        filtro
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_SUBSIDIARIA_DETALHAR')"
    )
    public ResponseEntity<DetalheSubsidiariaRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @PutMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_SUBSIDIARIA_EDITAR')"
    )
    public ResponseEntity<DetalheSubsidiariaRecord> atualizar(
            @RequestBody @Valid AtualizaSubsidiariaRecord dados
    ) {
        return ResponseEntity.ok(
                service.atualizar(dados)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_SUBSIDIARIA_EXCLUIR')"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}