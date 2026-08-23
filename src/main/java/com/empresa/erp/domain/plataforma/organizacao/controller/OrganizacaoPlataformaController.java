package com.empresa.erp.domain.plataforma.organizacao.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.domain.plataforma.organizacao.record.DetalheOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.ListaOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.OrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.service.OrganizacaoPlataformaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/plataforma/organizacao")
@RequiredArgsConstructor
public class OrganizacaoPlataformaController {

    private final OrganizacaoPlataformaService service;

    @GetMapping
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_LISTAR')"
    )
    public ResponseEntity<Page<ListaOrganizacaoRecord>>
            listar(
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
                service.listar(
                        paginacao,
                        filtro
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_DETALHAR')"
    )
    public ResponseEntity<DetalheOrganizacaoRecord>
            detalhar(
                    @PathVariable
                    Long id
            ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_EDITAR')"
    )
    public ResponseEntity<DetalheOrganizacaoRecord>
            editar(
                    @PathVariable
                    Long id,

                    @RequestBody
                    @Valid
                    OrganizacaoRecord dados
            ) {
        return ResponseEntity.ok(
                service.editar(
                        id,
                        dados
                )
        );
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_STATUS')"
    )
    public ResponseEntity<DetalheOrganizacaoRecord>
            inativar(
                    @PathVariable
                    Long id
            ) {
        return ResponseEntity.ok(
                service.inativar(id)
        );
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_STATUS')"
    )
    public ResponseEntity<DetalheOrganizacaoRecord>
            reativar(
                    @PathVariable
                    Long id
            ) {
        return ResponseEntity.ok(
                service.reativar(id)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('PLATAFORMA_ORGANIZACAO_EXCLUIR')"
    )
    public ResponseEntity<Void> remover(
            @PathVariable
            Long id
    ) {
        service.remover(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}