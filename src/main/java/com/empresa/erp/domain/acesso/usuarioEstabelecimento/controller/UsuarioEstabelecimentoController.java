package com.empresa.erp.domain.acesso.usuarioEstabelecimento.controller;

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

import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.DetalheUsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.ListaUsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.UsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.service.UsuarioEstabelecimentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/acesso/usuario-estabelecimento")
@RequiredArgsConstructor
public class UsuarioEstabelecimentoController {

    private final UsuarioEstabelecimentoService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_ESTABELECIMENTO_CRIAR')")
    public ResponseEntity<DetalheUsuarioEstabelecimentoRecord> cadastrar(
            @RequestBody @Valid UsuarioEstabelecimentoRecord dados,
            UriComponentsBuilder uriBuilder
    ) {
        UsuarioEstabelecimentoModel usuarioEstabelecimento =
                service.cadastrar(dados);

        var uri = uriBuilder
                .path("/acesso/usuario-estabelecimento/{id}")
                .buildAndExpand(usuarioEstabelecimento.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(
                        new DetalheUsuarioEstabelecimentoRecord(
                                usuarioEstabelecimento
                        )
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_ESTABELECIMENTO_LISTAR')")
    public ResponseEntity<Page<ListaUsuarioEstabelecimentoRecord>> listar(
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
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_ESTABELECIMENTO_DETALHAR')")
    public ResponseEntity<DetalheUsuarioEstabelecimentoRecord> detalhar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.detalhar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_ESTABELECIMENTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}