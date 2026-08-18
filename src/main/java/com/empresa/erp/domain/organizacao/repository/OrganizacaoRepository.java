package com.empresa.erp.domain.organizacao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

public interface OrganizacaoRepository
        extends JpaRepository<OrganizacaoModel, Long> {

    boolean existsByIdAndStatus(
            Long id,
            StatusEnum status
    );
}