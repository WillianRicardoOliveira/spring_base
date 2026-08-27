package com.empresa.erp.domain.acesso.usuarioEmpresa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.base.model.StatusEnum;

public interface UsuarioEmpresaRepository
        extends JpaRepository<UsuarioEmpresaModel, Long> {

    @EntityGraph(
            attributePaths = {
                    "usuarioOrganizacao.usuario",
                    "empresa"
            }
    )
    @Query("""
            SELECT usuarioEmpresa
            FROM UsuarioEmpresaModel usuarioEmpresa
            WHERE usuarioEmpresa.usuarioOrganizacao.organizacao.id =
                    :idOrganizacao
              AND usuarioEmpresa.empresa.organizacao.id =
                    :idOrganizacao
              AND usuarioEmpresa.usuarioOrganizacao.status =
                    :status
              AND usuarioEmpresa.usuarioOrganizacao.usuario.status =
                    :status
              AND usuarioEmpresa.status = :status
              AND (
                    :idUsuarioOrganizacao IS NULL
                    OR usuarioEmpresa.usuarioOrganizacao.id =
                        :idUsuarioOrganizacao
              )
              AND (
                    :idEmpresa IS NULL
                    OR usuarioEmpresa.empresa.id = :idEmpresa
              )
            """)
    Page<UsuarioEmpresaModel>
            buscarAtivosDaOrganizacao(
                    Pageable paginacao,

                    @Param("idOrganizacao")
                    Long idOrganizacao,

                    @Param("idUsuarioOrganizacao")
                    Long idUsuarioOrganizacao,

                    @Param("idEmpresa")
                    Long idEmpresa,

                    @Param("status")
                    StatusEnum status
            );

    boolean
            existsByUsuarioOrganizacaoIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                    Long idUsuarioOrganizacao,
                    Long idEmpresa,
                    Long idOrganizacao,
                    StatusEnum status
            );

    @EntityGraph(
            attributePaths = {
                    "usuarioOrganizacao.usuario",
                    "empresa"
            }
    )
    Optional<UsuarioEmpresaModel>
            findByIdAndUsuarioOrganizacaoOrganizacaoIdAndEmpresaOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacaoUsuario,
                    Long idOrganizacaoEmpresa,
                    StatusEnum status
            );

    boolean existsByEmpresaIdAndStatus(
            Long idEmpresa,
            StatusEnum status
    );
}