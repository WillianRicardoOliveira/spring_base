package com.empresa.erp.domain.acesso.usuarioPerfil.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.repository.PerfilRepository;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.DetalheUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.ListaUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioPerfilService {

    private final UsuarioPerfilRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    @Transactional
    public UsuarioPerfilModel cadastrar(UsuarioPerfilRecord dados) {
        UsuarioModel usuario = usuarioRepository.findByIdAndStatus(dados.idUsuario(), StatusEnum.ATIVO).orElseThrow(() -> new ValidacaoException("Usuario nao encontrado."));

        PerfilModel perfil = perfilRepository.findByIdAndStatus(dados.idPerfil(), StatusEnum.ATIVO).orElseThrow(() -> new ValidacaoException("Perfil nao encontrado."));

        if (repository.existsByUsuarioAndPerfilAndStatus(usuario, perfil, StatusEnum.ATIVO)) {
            throw new ValidacaoException("Perfil ja vinculado ao usuario.");
        }

        UsuarioPerfilModel usuarioPerfil = new UsuarioPerfilModel(usuario, perfil);
        repository.save(usuarioPerfil);
        return usuarioPerfil;
    }

    @Transactional(readOnly = true)
    public List<ListaUsuarioPerfilRecord> listarPorUsuario(Long idUsuario) {
        return repository.findAllByUsuarioIdAndStatus(idUsuario, StatusEnum.ATIVO).stream().map(ListaUsuarioPerfilRecord::new).toList();
    }

    @Transactional
    public void excluir(Long id) {
        repository.getReferenceById(id).remover();
    }

    @Transactional(readOnly = true)
    public DetalheUsuarioPerfilRecord detalhar(Long id) {
        UsuarioPerfilModel usuarioPerfil = repository.getReferenceById(id);
        return new DetalheUsuarioPerfilRecord(usuarioPerfil);
    }
    
}