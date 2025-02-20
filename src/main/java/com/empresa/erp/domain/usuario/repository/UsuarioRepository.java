package com.empresa.erp.domain.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

	UserDetails findByEmail(String username);

}