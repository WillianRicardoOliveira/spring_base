package com.empresa.erp.modulos.fiscal.endereco.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.modulos.fiscal.endereco.model.EnderecoModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public interface EnderecoRepository extends JpaRepository<EnderecoModel, Long> {
	
	// Page<EnderecoModel> findAllByStatusAndCriacaoAfter(Pageable paginacao, StatusEnum status, LocalDateTime criacao);
	
	// Page<EnderecoModel> findByLogradouroContainingAndStatusAndCriacaoAfter(Pageable paginacao, String filtro, StatusEnum status, LocalDateTime criacao);
	
}