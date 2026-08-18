package com.empresa.erp.domain.acesso.usuarioOrganizacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface UsuarioOrganizacaoRepository
        extends JpaRepository<UsuarioOrganizacaoModel, Long> {

    List<UsuarioOrganizacaoModel>
            findAllByUsuarioIdAndStatus(
                    Long idUsuario,
                    StatusEnum status
            );

    @EntityGraph(attributePaths = "organizacao")
    List<UsuarioOrganizacaoModel>
            findAllByUsuarioIdAndStatusAndOrganizacaoStatusOrderByOrganizacaoNomeAsc(
                    Long idUsuario,
                    StatusEnum statusVinculo,
                    StatusEnum statusOrganizacao
            );

    Optional<UsuarioOrganizacaoModel>
            findByUsuarioIdAndOrganizacaoId(
                    Long idUsuario,
                    Long idOrganizacao
            );

    Optional<UsuarioOrganizacaoModel>
            findByUsuarioIdAndOrganizacaoIdAndStatus(
                    Long idUsuario,
                    Long idOrganizacao,
                    StatusEnum status
            );

    boolean existsByUsuarioIdAndOrganizacaoIdAndStatus(
            Long idUsuario,
            Long idOrganizacao,
            StatusEnum status
    );
}