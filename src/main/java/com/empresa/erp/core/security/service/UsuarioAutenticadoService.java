package com.empresa.erp.core.security.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioAutenticadoService {

    private final UsuarioRepository
            usuarioRepository;

    @Transactional(readOnly = true)
    public UsuarioAutenticado buscarPorEmail(
            String email
    ) {
        UsuarioModel usuario =
                usuarioRepository.findByEmailIgnoreCase(
                        email
                );

        if (usuario == null || !usuario.isEnabled()) {
            return null;
        }

        return new UsuarioAutenticado(
                usuario,
                List.of()
        );
    }
}