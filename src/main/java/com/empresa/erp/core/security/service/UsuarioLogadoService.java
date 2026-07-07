package com.empresa.erp.core.security.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.model.UsuarioAutenticado;

@Service
public class UsuarioLogadoService {

    public Long getId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ValidacaoException("Usuario nao autenticado.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UsuarioAutenticado usuarioAutenticado) {
            return usuarioAutenticado.getId();
        }

        throw new ValidacaoException("Usuario autenticado invalido.");
    }
}