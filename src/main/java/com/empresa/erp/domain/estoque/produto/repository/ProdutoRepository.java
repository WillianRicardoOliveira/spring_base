package com.empresa.erp.domain.estoque.produto.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.estoque.produto.model.ProdutoModel;

public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {
	
	Page<ProdutoModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<ProdutoModel> findAllByAtivoTrue(Pageable paginacao);
		
}