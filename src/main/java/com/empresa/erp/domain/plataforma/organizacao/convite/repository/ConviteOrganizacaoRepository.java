package com.empresa.erp.domain.plataforma.organizacao.convite.repository;

import java.util.Optional;

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
}