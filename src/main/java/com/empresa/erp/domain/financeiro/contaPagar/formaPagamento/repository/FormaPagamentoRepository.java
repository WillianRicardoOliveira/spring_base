package com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.financeiro.contaPagar.formaPagamento.model.FormaPagamentoModel;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamentoModel, Long> {
	
	Page<FormaPagamentoModel> findByNomeContaining(Pageable paginacao, String filtro);
		
	Page<FormaPagamentoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
