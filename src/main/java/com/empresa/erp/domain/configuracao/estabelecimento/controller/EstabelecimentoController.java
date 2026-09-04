package com.empresa.erp.domain.configuracao.estabelecimento.controller;

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

import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.record.AtualizaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.DetalheEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.EstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.ListaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.service.EstabelecimentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/configuracao/estabelecimento")
@RequiredArgsConstructor
public class EstabelecimentoController {

    private final EstabelecimentoService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_ESTABELECIMENTO_CRIAR')"
    )
    public ResponseEntity<DetalheEstabelecimentoRecord> cadastrar(
            @RequestBody @Valid EstabelecimentoRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        EstabelecimentoModel estabelecimento =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path("/configuracao/estabelecimento/{id}")
                .buildAndExpand(estabelecimento.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(
                        new DetalheEstabelecimentoRecord(
                        		estabelecimento
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_ESTABELECIMENTO_LISTAR')"
    )
    public ResponseEntity<Page<ListaEstabelecimentoRecord>> listar(
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
            "hasAuthority('CONFIGURACAO_ESTABELECIMENTO_DETALHAR')"
    )
    public ResponseEntity<DetalheEstabelecimentoRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @PutMapping
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_ESTABELECIMENTO_EDITAR')"
    )
    public ResponseEntity<DetalheEstabelecimentoRecord> atualizar(
            @RequestBody @Valid AtualizaEstabelecimentoRecord dados
    ) {
        return ResponseEntity.ok(
                service.atualizar(dados)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('CONFIGURACAO_ESTABELECIMENTO_EXCLUIR')"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}