package com.empresa.erp.domain.estoque.movimentacao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.estoque.movimentacao.model.MovimentacaoModel;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoModel, Long> {
	
	Page<MovimentacaoModel> findByProdutoNomeContaining(Pageable paginacao, String filtro);
	
	Page<MovimentacaoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
