package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaContaRepository extends JpaRepository<CategoriaContaModel, Long> {
	
	Page<CategoriaContaModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<CategoriaContaModel> findAllByAtivoTrue(Pageable paginacao);
	
}
