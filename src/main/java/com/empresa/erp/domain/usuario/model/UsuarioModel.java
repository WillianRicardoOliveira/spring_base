package com.empresa.erp.domain.usuario.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "usuario")
@Entity(name = "UsuarioModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UsuarioModel implements UserDetails {
	
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String senha;
    
    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public UsuarioModel(UsuarioRecord dados, String senhaCriptografada) {
        this.email = normalizarEmail(dados.email());
        this.senha = senhaCriptografada;
        this.status = StatusEnum.ATIVO;
    }

    public void atualizar(AtualizaUsuarioRecord dados) {
        this.email = normalizarEmail(dados.email());
    }

    public void atualizarSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }

    public void inativar() {
    	this.status = StatusEnum.INATIVO;
    }
    
    public void remover() {
        this.status = StatusEnum.REMOVIDO;
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
    	return senha;
    }
    
    @Override
    public String getUsername() {
    	return email;
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
        return StatusEnum.ATIVO.equals(this.status);
    }
    
}