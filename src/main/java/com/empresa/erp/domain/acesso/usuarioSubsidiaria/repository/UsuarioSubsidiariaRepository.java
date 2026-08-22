package com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface UsuarioSubsidiariaRepository
        extends JpaRepository<UsuarioSubsidiariaModel, Long> {

    @EntityGraph(
            attributePaths = {
                    "usuarioEmpresa.usuarioOrganizacao.usuario",
                    "usuarioEmpresa.empresa",
                    "subsidiaria"
            }
    )
    @Query("""
            SELECT usuarioSubsidiaria
            FROM UsuarioSubsidiariaModel usuarioSubsidiaria
            WHERE usuarioSubsidiaria
                    .usuarioEmpresa
                    .usuarioOrganizacao
                    .organizacao
                    .id = :idOrganizacao
              AND usuarioSubsidiaria
                    .usuarioEmpresa
                    .empresa
                    .organizacao
                    .id = :idOrganizacao
              AND usuarioSubsidiaria
                    .subsidiaria
                    .empresa
                    .organizacao
                    .id = :idOrganizacao
              AND usuarioSubsidiaria.subsidiaria.empresa =
                    usuarioSubsidiaria.usuarioEmpresa.empresa
              AND usuarioSubsidiaria
                    .usuarioEmpresa
                    .usuarioOrganizacao
                    .status = :status
              AND usuarioSubsidiaria
                    .usuarioEmpresa
                    .usuarioOrganizacao
                    .usuario
                    .status = :status
              AND usuarioSubsidiaria
                    .usuarioEmpresa
                    .status = :status
              AND usuarioSubsidiaria
                    .usuarioEmpresa
                    .empresa
                    .status = :status
              AND usuarioSubsidiaria
                    .subsidiaria
                    .status = :status
              AND usuarioSubsidiaria.status = :status
              AND (
                    :idUsuarioEmpresa IS NULL
                    OR usuarioSubsidiaria.usuarioEmpresa.id =
                        :idUsuarioEmpresa
              )
            """)
    Page<UsuarioSubsidiariaModel>
            buscarAtivosDaOrganizacao(
                    Pageable paginacao,

                    @Param("idOrganizacao")
                    Long idOrganizacao,

                    @Param("idUsuarioEmpresa")
                    Long idUsuarioEmpresa,

                    @Param("status")
                    StatusEnum status
            );

    boolean existsByUsuarioEmpresaAndSubsidiariaAndStatus(
            UsuarioEmpresaModel usuarioEmpresa,
            SubsidiariaModel subsidiaria,
            StatusEnum status
    );

    boolean existsByUsuarioEmpresaIdAndStatus(
            Long idUsuarioEmpresa,
            StatusEnum status
    );

    boolean existsBySubsidiariaIdAndStatus(
            Long idSubsidiaria,
            StatusEnum status
    );

    @EntityGraph(
            attributePaths = {
                    "usuarioEmpresa.usuarioOrganizacao.usuario",
                    "usuarioEmpresa.empresa",
                    "subsidiaria"
            }
    )
    Optional<UsuarioSubsidiariaModel>
            findByIdAndUsuarioEmpresaUsuarioOrganizacaoOrganizacaoIdAndUsuarioEmpresaEmpresaOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacaoUsuario,
                    Long idOrganizacaoEmpresa,
                    StatusEnum status
            );
}