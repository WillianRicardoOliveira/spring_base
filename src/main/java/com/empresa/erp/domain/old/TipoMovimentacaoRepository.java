package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMovimentacaoRepository extends JpaRepository<TipoMovimentacaoModel, Long> {
	
	Page<TipoMovimentacaoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
