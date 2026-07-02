package com.empresa.erp.domain.acesso.perfil.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface PerfilRepository extends JpaRepository<PerfilModel, Long> {

	Page<PerfilModel> findByNomeContainingIgnoreCaseAndStatus(Pageable paginacao, String filtro, StatusEnum status);

	Page<PerfilModel> findAllByStatus(Pageable paginacao, StatusEnum status);

	boolean existsByNomeIgnoreCaseAndStatus(String nome, StatusEnum status);

	boolean existsByNomeIgnoreCaseAndStatusAndIdNot(String nome, StatusEnum status, Long id);
	
	Optional<PerfilModel> findByIdAndStatus(Long id, StatusEnum status);
	
}