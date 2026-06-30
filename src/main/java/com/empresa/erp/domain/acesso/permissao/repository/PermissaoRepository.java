package com.empresa.erp.domain.acesso.permissao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public interface PermissaoRepository extends JpaRepository<PermissaoModel, Long> {

    Page<PermissaoModel> findByNomeContainingIgnoreCaseAndStatus(Pageable paginacao, String filtro, StatusEnum status);

    Page<PermissaoModel> findAllByStatus(Pageable paginacao, StatusEnum status);

    boolean existsByChaveIgnoreCaseAndStatus(String chave, StatusEnum status);

    boolean existsByChaveIgnoreCaseAndStatusAndIdNot(String chave, StatusEnum status, Long id);
    
}