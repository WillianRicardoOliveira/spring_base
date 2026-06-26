package com.empresa.erp.domain.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

	UsuarioModel findByEmail(String username);

}
