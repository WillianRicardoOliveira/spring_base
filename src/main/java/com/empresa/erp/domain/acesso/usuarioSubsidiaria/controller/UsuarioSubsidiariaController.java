package com.empresa.erp.domain.acesso.usuarioSubsidiaria.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.DetalheUsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.ListaUsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.record.UsuarioSubsidiariaRecord;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.service.UsuarioSubsidiariaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/acesso/usuario-subsidiaria")
@RequiredArgsConstructor
public class UsuarioSubsidiariaController {

    private final UsuarioSubsidiariaService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_SUBSIDIARIA_CRIAR')"
    )
    public ResponseEntity<DetalheUsuarioSubsidiariaRecord>
            cadastrar(
                    @RequestBody @Valid
                    UsuarioSubsidiariaRecord dados,
                    UriComponentsBuilder uriBuilder
            ) {
        UsuarioSubsidiariaModel usuarioSubsidiaria =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path(
                        "/acesso/usuario-subsidiaria/{id}"
                )
                .buildAndExpand(
                        usuarioSubsidiaria.getId()
                )
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(
                        new DetalheUsuarioSubsidiariaRecord(
                                usuarioSubsidiaria
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_SUBSIDIARIA_LISTAR')"
    )
    public ResponseEntity<
            Page<ListaUsuarioSubsidiariaRecord>
    > listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable paginacao,

            @RequestParam(
                    name = "idUsuarioEmpresa",
                    required = false
            )
            Long idUsuarioEmpresa
    ) {
        return ResponseEntity.ok(
                service.listar(
                        paginacao,
                        idUsuarioEmpresa
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_SUBSIDIARIA_DETALHAR')"
    )
    public ResponseEntity<DetalheUsuarioSubsidiariaRecord>
            detalhar(
                    @PathVariable Long id
            ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_SUBSIDIARIA_EXCLUIR')"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}