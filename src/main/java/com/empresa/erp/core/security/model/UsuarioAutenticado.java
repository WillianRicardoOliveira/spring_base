package com.empresa.erp.core.security.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public class UsuarioAutenticado implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final UsuarioModel usuario;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioAutenticado(UsuarioModel usuario, Collection<? extends GrantedAuthority> authorities) {
        this.usuario = usuario;
        this.authorities = authorities;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public Long getId() {
        return usuario.getId();
    }

    public String getEmail() {
        return usuario.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return StatusEnum.ATIVO.equals(usuario.getStatus());
    }
    
}