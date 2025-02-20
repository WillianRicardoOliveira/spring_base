package com.empresa.erp.domain.estoque.compra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.estoque.compra.model.CompraModel;

public interface CompraRepository extends JpaRepository<CompraModel, Long> {
	
	Page<CompraModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<CompraModel> findAllByAtivoTrue(Pageable paginacao);
	
}
