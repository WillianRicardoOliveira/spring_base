package com.empresa.erp.domain.acesso.perfil.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.base.model.StatusEnum;

import jakarta.persistence.LockModeType;

public interface PerfilRepository
        extends JpaRepository<PerfilModel, Long> {

    Page<PerfilModel>
            findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    String filtro,
                    StatusEnum status
            );

    Page<PerfilModel> findAllByOrganizacaoIdAndStatus(
            Pageable paginacao,
            Long idOrganizacao,
            StatusEnum status
    );

    boolean existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
            Long idOrganizacao,
            String nome,
            StatusEnum status
    );

    boolean
            existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                    Long idOrganizacao,
                    String nome,
                    StatusEnum status,
                    Long id
            );

    Optional<PerfilModel>
            findByIdAndOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacao,
                    StatusEnum status
            );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT perfil
            FROM PerfilModel perfil
            WHERE perfil.id = :id
              AND perfil.organizacao.id = :idOrganizacao
              AND perfil.status = :status
            """)
    Optional<PerfilModel>
            buscarPorIdEOrganizacaoEStatusParaAtualizacao(
                    @Param("id")
                    Long id,

                    @Param("idOrganizacao")
                    Long idOrganizacao,

                    @Param("status")
                    StatusEnum status
            );
}