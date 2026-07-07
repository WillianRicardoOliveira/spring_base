package com.empresa.erp.core.security.service;

import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.domain.acesso.usuarioPerfil.repository.UsuarioPerfilRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPerfilRepository usuarioPerfilRepository;

    @Transactional(readOnly = true)
    public UsuarioAutenticado buscarPorEmail(String email) {
        UsuarioModel usuario = usuarioRepository.findByEmailIgnoreCase(email);

        if (usuario == null || !usuario.isEnabled()) {
            return null;
        }

        Set<String> permissoes = usuarioPerfilRepository.buscarChavesPermissoesAtivasPorUsuario(usuario.getId(), StatusEnum.ATIVO);

        var authorities = permissoes.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsuarioAutenticado(usuario, authorities);
    }
    
}