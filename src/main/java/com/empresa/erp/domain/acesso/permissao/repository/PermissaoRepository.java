package com.empresa.erp.domain.acesso.permissao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;

public interface PermissaoRepository
        extends JpaRepository<PermissaoModel, Long> {

    Page<PermissaoModel>
            findByNomeContainingIgnoreCaseAndEscopoAndStatus(
                    Pageable paginacao,
                    String filtro,
                    EscopoPermissaoEnum escopo,
                    StatusEnum status
            );

    Page<PermissaoModel> findAllByEscopoAndStatus(
            Pageable paginacao,
            EscopoPermissaoEnum escopo,
            StatusEnum status
    );

    List<PermissaoModel>
            findAllBySistemaTrueAndEscopoAndStatusOrderByIdAsc(
                    EscopoPermissaoEnum escopo,
                    StatusEnum status
            );

    Optional<PermissaoModel> findByIdAndEscopoAndStatus(
            Long id,
            EscopoPermissaoEnum escopo,
            StatusEnum status
    );
}