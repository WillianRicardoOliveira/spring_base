package com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.financeiro.contaPagar.categoriaConta.model.CategoriaContaModel;

public interface CategoriaContaRepository extends JpaRepository<CategoriaContaModel, Long> {
	
	Page<CategoriaContaModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<CategoriaContaModel> findAllByAtivoTrue(Pageable paginacao);
	
}
