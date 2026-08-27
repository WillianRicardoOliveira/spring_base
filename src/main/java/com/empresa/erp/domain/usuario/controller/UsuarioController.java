package com.empresa.erp.domain.usuario.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.usuario.record.DetalheUsuarioRecord;
import com.empresa.erp.domain.usuario.record.ListaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_CRIAR')"
    )
    public ResponseEntity<DetalheUsuarioRecord> cadastrar(
            @RequestBody @Valid UsuarioRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        UsuarioOrganizacaoModel usuarioOrganizacao =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path("/usuario/{id}")
                .buildAndExpand(
                        usuarioOrganizacao
                                .getUsuario()
                                .getId()
                )
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(
                        new DetalheUsuarioRecord(
                                usuarioOrganizacao
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_LISTAR')"
    )
    public ResponseEntity<Page<ListaUsuarioRecord>> listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable paginacao,

            @RequestParam(required = false)
            String filtro,

            @RequestParam(required = false)
            StatusEnum status
    ) {
        return ResponseEntity.ok(
                service.listar(
                        paginacao,
                        filtro,
                        status
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_EXCLUIR')"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity
                .noContent()
                .build();
    }
    
    @PatchMapping("/{id}/reativar")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_CRIAR')"
    )
    public ResponseEntity<DetalheUsuarioRecord> reativar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.reativar(id)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_DETALHAR')"
    )
    public ResponseEntity<DetalheUsuarioRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }
}