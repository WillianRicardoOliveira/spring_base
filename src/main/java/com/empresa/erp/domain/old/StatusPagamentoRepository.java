package com.empresa.erp.domain.old;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusPagamentoRepository extends JpaRepository<StatusPagamentoModel, Long> {
	
	Page<StatusPagamentoModel> findByNomeContaining(Pageable paginacao, String filtro);
		
	Page<StatusPagamentoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
