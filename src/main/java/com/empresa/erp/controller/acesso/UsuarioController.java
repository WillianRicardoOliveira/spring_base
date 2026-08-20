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

import com.empresa.erp.domain.usuario.model.UsuarioModel;
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
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_CRIAR')")
    public ResponseEntity<DetalheUsuarioRecord> cadastrar(
            @RequestBody @Valid UsuarioRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        UsuarioModel usuario =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path("/usuario/{id}")
                .buildAndExpand(usuario.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(new DetalheUsuarioRecord(usuario));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_LISTAR')")
    public ResponseEntity<Page<ListaUsuarioRecord>> listar(
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_EXCLUIR')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_DETALHAR')")
    public ResponseEntity<DetalheUsuarioRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                service.detalhar(id)
        );
    }
}