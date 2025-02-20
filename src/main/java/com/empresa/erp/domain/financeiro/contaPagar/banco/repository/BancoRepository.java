package com.empresa.erp.domain.financeiro.contaPagar.banco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.financeiro.contaPagar.banco.model.BancoModel;

public interface BancoRepository extends JpaRepository<BancoModel, Long> {
	
	Page<BancoModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<BancoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
