package com.empresa.erp.modulos.fiscal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.modulos.fiscal.model.EntidadeModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public interface EntidadeRepository extends JpaRepository<EntidadeModel, Long> {

	Page<EntidadeModel> findByNomeCompletoContaining(Pageable paginacao, String filtro);
	
	Page<EntidadeModel> findAllByStatus(Pageable paginacao, StatusEnum status);	
	
}