package com.empresa.erp.domain.organizacao.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

import jakarta.persistence.LockModeType;

public interface OrganizacaoRepository
        extends JpaRepository<OrganizacaoModel, Long> {

    Page<OrganizacaoModel>
            findByNomeContainingIgnoreCase(
                    Pageable paginacao,
                    String filtro
            );

    boolean existsByIdAndStatus(
            Long id,
            StatusEnum status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT organizacao
            FROM OrganizacaoModel organizacao
            WHERE organizacao.id = :id
              AND organizacao.status = :status
            """)
    Optional<OrganizacaoModel>
            buscarPorIdEStatusParaAtualizacao(
                    @Param("id")
                    Long id,

                    @Param("status")
                    StatusEnum status
            );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT organizacao
            FROM OrganizacaoModel organizacao
            WHERE organizacao.id = :id
              AND organizacao.status <> :statusRemovido
            """)
    Optional<OrganizacaoModel>
            buscarPorIdNaoRemovidoParaAtualizacao(
                    @Param("id")
                    Long id,

                    @Param("statusRemovido")
                    StatusEnum statusRemovido
            );
}