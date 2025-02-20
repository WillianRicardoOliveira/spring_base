package com.empresa.erp.domain.fiscal.setorAtividade.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.fiscal.setorAtividade.model.SetorAtividadeModel;

public interface SetorAtividadeRepository extends JpaRepository<SetorAtividadeModel, Long> {

	Page<SetorAtividadeModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<SetorAtividadeModel> findAllByAtivoTrue(Pageable paginacao);
	
}
