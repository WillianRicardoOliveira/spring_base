package com.empresa.erp.domain.fiscal.regimeTributacaoFederal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.fiscal.regimeTributacaoFederal.model.RegimeTributacaoFederalModel;

public interface RegimeTributacaoFederalRepository extends JpaRepository<RegimeTributacaoFederalModel, Long> {

	Page<RegimeTributacaoFederalModel> findByNomeContaining(Pageable paginacao, String filtro);
	
	Page<RegimeTributacaoFederalModel> findAllByAtivoTrue(Pageable paginacao);
	
}
