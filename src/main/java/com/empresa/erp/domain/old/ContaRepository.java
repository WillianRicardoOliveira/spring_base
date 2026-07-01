package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<ContaModel, Long> {
	
	Page<ContaModel> findByContaContaining(Pageable paginacao, String filtro);
	
	Page<ContaModel> findAllByAtivoTrue(Pageable paginacao);
	
}
