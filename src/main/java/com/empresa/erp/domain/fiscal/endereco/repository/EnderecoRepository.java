package com.empresa.erp.domain.fiscal.endereco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.fiscal.endereco.model.EnderecoModel;

public interface EnderecoRepository extends JpaRepository<EnderecoModel, Long> {

	Page<EnderecoModel> findByLogradouroContaining(Pageable paginacao, String filtro);
	
	Page<EnderecoModel> findAllByAtivoTrue(Pageable paginacao);
	
}
