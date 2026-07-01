package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubCategoriaContaRepository extends JpaRepository<SubCategoriaContaModel, Long> {
	
	Page<SubCategoriaContaModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<SubCategoriaContaModel> findAllByAtivoTrue(Pageable paginacao);
	
}
