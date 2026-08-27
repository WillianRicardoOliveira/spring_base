package com.empresa.erp.domain.acesso.usuarioOrganizacao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;

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

    @EntityGraph(attributePaths = "usuario")
    Page<UsuarioOrganizacaoModel>
            findAllByOrganizacaoIdAndStatusAndUsuarioStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    StatusEnum statusVinculo,
                    StatusEnum statusUsuario
            );

    @EntityGraph(attributePaths = "usuario")
    Page<UsuarioOrganizacaoModel>
            findByOrganizacaoIdAndUsuarioEmailContainingIgnoreCaseAndStatusAndUsuarioStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    String filtro,
                    StatusEnum statusVinculo,
                    StatusEnum statusUsuario
            );

    @EntityGraph(attributePaths = "usuario")
    Optional<UsuarioOrganizacaoModel>
            findByUsuarioIdAndOrganizacaoIdAndStatusAndUsuarioStatus(
                    Long idUsuario,
                    Long idOrganizacao,
                    StatusEnum statusVinculo,
                    StatusEnum statusUsuario
            );
}