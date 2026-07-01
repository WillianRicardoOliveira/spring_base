package com.empresa.erp.domain.usuario.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

	UsuarioModel findByEmailIgnoreCase(String email);

	Page<UsuarioModel> findAllByStatus(Pageable paginacao, StatusEnum status);

	Page<UsuarioModel> findByEmailContainingIgnoreCaseAndStatus(Pageable paginacao, String filtro, StatusEnum status);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    
}