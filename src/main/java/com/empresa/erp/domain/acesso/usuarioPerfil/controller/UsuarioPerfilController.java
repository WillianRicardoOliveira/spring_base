package com.empresa.erp.controller.acesso;

import java.util.List;

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

import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.DetalheUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.ListaUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.service.UsuarioPerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuario-perfil")
@RequiredArgsConstructor
public class UsuarioPerfilController {

    private final UsuarioPerfilService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_PERFIL_CRIAR')")
    public ResponseEntity<DetalheUsuarioPerfilRecord> cadastrar(@RequestBody @Valid UsuarioPerfilRecord dados, UriComponentsBuilder uriBuilder) {
        UsuarioPerfilModel usuarioPerfil = service.cadastrar(dados);
        var uri = uriBuilder.path("/usuario-perfil/{id}").buildAndExpand(usuarioPerfil.getId()).toUri();
        return ResponseEntity.created(uri).body(new DetalheUsuarioPerfilRecord(usuarioPerfil));
    }

    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_PERFIL_LISTAR')")
    public ResponseEntity<List<ListaUsuarioPerfilRecord>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_PERFIL_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACESSO_USUARIO_PERFIL_DETALHAR')")
    public ResponseEntity<DetalheUsuarioPerfilRecord> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(service.detalhar(id));
    }
    
}