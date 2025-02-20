package com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.financeiro.contaPagar.statusPagamento.model.StatusPagamentoModel;

public interface StatusPagamentoRepository extends JpaRepository<StatusPagamentoModel, Long> {
	
	Page<StatusPagamentoModel> findByNomeContaining(Pageable paginacao, String filtro);
		
	Page<StatusPagamentoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
