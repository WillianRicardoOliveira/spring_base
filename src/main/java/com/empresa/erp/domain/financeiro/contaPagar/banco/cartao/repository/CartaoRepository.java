package com.empresa.erp.domain.financeiro.contaPagar.banco.cartao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.financeiro.contaPagar.banco.cartao.model.CartaoModel;

public interface CartaoRepository extends JpaRepository<CartaoModel, Long> {
	
	Page<CartaoModel> findByNumeroCartaoContaining(Pageable paginacao, String filtro);
	
	Page<CartaoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
