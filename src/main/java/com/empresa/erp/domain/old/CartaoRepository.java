package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<CartaoModel, Long> {
	
	Page<CartaoModel> findByNumeroCartaoContaining(Pageable paginacao, String filtro);
	
	Page<CartaoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
