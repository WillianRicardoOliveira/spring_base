package com.empresa.erp.domain.acesso.usuarioEmpresa.controller;

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

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.DetalheUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.ListaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.UsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.service.UsuarioEmpresaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/acesso/usuario-empresa")
@RequiredArgsConstructor
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService service;

    @PostMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_EMPRESA_CRIAR')"
    )
    public ResponseEntity<DetalheUsuarioEmpresaRecord> cadastrar(
            @RequestBody @Valid UsuarioEmpresaRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        UsuarioEmpresaModel usuarioEmpresa =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path("/acesso/usuario-empresa/{id}")
                .buildAndExpand(usuarioEmpresa.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(
                        new DetalheUsuarioEmpresaRecord(
                                usuarioEmpresa
                        )
                );
    }

    @GetMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_EMPRESA_LISTAR')"
    )
    public ResponseEntity<Page<ListaUsuarioEmpresaRecord>> listar(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.DESC
            )
            Pageable paginacao,

            @RequestParam(
                    name = "idUsuario",
                    required = false
            )
            Long idUsuario,

            @RequestParam(
                    name = "idEmpresa",
                    required = false
            )
            Long idEmpresa
    ) {
        return ResponseEntity.ok(
                service.listar(
                        paginacao,
                        idUsuario,
                        idEmpresa
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_EMPRESA_DETALHAR')"
    )
    public ResponseEntity<DetalheUsuarioEmpresaRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }

    @PutMapping
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_EMPRESA_EDITAR')"
    )
    public ResponseEntity<DetalheUsuarioEmpresaRecord> atualizar(
            @RequestBody @Valid
            AtualizaUsuarioEmpresaRecord dados
    ) {
        return ResponseEntity.ok(
                service.atualizar(dados)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ACESSO_USUARIO_EMPRESA_EXCLUIR')"
    )
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}