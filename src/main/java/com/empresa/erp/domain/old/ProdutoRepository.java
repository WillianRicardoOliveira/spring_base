package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {
	
	Page<ProdutoModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<ProdutoModel> findAllByAtivoTrue(Pageable paginacao);
		
}