package com.empresa.erp.domain.old;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoRepository extends JpaRepository<EnderecoModel, Long> {
	
	// Page<EnderecoModel> findAllByStatusAndCriacaoAfter(Pageable paginacao, StatusEnum status, LocalDateTime criacao);
	
	// Page<EnderecoModel> findByLogradouroContainingAndStatusAndCriacaoAfter(Pageable paginacao, String filtro, StatusEnum status, LocalDateTime criacao);
	
}