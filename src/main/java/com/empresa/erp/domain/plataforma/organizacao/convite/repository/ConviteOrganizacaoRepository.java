package com.empresa.erp.domain.plataforma.organizacao.convite.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;

import jakarta.persistence.LockModeType;

public interface ConviteOrganizacaoRepository
        extends JpaRepository<
                ConviteOrganizacaoModel,
                Long
        > {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT convite
            FROM ConviteOrganizacaoModel convite
            WHERE convite.emailPendente = :emailAdministrador
            """)
    Optional<ConviteOrganizacaoModel>
            buscarPendentePorEmailParaAtualizacao(
                    @Param("emailAdministrador")
                    String emailAdministrador
            );

    @Query("""
            SELECT convite
            FROM ConviteOrganizacaoModel convite
            WHERE convite.tokenHash = :tokenHash
              AND convite.status = :status
            """)
    Optional<ConviteOrganizacaoModel>
            buscarPorTokenHashEStatus(
                    @Param("tokenHash")
                    String tokenHash,

                    @Param("status")
                    StatusConviteOrganizacaoEnum status
            );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT convite
            FROM ConviteOrganizacaoModel convite
            WHERE convite.tokenHash = :tokenHash
              AND convite.status = :status
            """)
    Optional<ConviteOrganizacaoModel>
            buscarPorTokenHashEStatusParaAtualizacao(
                    @Param("tokenHash")
                    String tokenHash,

                    @Param("status")
                    StatusConviteOrganizacaoEnum status
            );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT convite
            FROM ConviteOrganizacaoModel convite
            WHERE convite.id = :id
            """)
    Optional<ConviteOrganizacaoModel>
            buscarPorIdParaAtualizacao(
                    @Param("id")
                    Long id
            );

    @Query("""
            SELECT convite
            FROM ConviteOrganizacaoModel convite
            WHERE (
                    :status IS NULL
                    OR convite.status = :status
                  )
              AND (
                    :filtro IS NULL
                    OR LOWER(convite.nomeOrganizacao)
                        LIKE LOWER(
                                CONCAT('%', :filtro, '%')
                        )
                    OR LOWER(convite.emailAdministrador)
                        LIKE LOWER(
                                CONCAT('%', :filtro, '%')
                        )
                  )
            """)
    Page<ConviteOrganizacaoModel> listar(
            Pageable paginacao,

            @Param("filtro")
            String filtro,

            @Param("status")
            StatusConviteOrganizacaoEnum status
    );
}