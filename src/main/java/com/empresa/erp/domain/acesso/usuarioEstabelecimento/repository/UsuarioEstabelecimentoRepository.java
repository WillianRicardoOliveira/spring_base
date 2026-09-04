package com.empresa.erp.domain.acesso.usuarioEstabelecimento.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;

public interface UsuarioEstabelecimentoRepository
        extends JpaRepository<UsuarioEstabelecimentoModel, Long> {

    @EntityGraph(
            attributePaths = {
                    "usuarioEmpresa.usuarioOrganizacao.usuario",
                    "usuarioEmpresa.empresa",
                    "estabelecimento"
            }
    )
    @Query("""
            SELECT usuarioEstabelecimento
            FROM UsuarioEstabelecimentoModel usuarioEstabelecimento
            WHERE usuarioEstabelecimento.usuarioEmpresa.usuarioOrganizacao.organizacao.id =
                    :idOrganizacao
              AND usuarioEstabelecimento.usuarioEmpresa.empresa.organizacao.id =
                    :idOrganizacao
              AND usuarioEstabelecimento.estabelecimento.empresa.organizacao.id =
                    :idOrganizacao
              AND usuarioEstabelecimento.estabelecimento.empresa =
                    usuarioEstabelecimento.usuarioEmpresa.empresa
              AND usuarioEstabelecimento.usuarioEmpresa.usuarioOrganizacao.status =
                    :status
              AND usuarioEstabelecimento.usuarioEmpresa.usuarioOrganizacao.usuario.status =
                    :status
              AND usuarioEstabelecimento.usuarioEmpresa.status =
                    :status
              AND usuarioEstabelecimento.usuarioEmpresa.empresa.status =
                    :status
              AND usuarioEstabelecimento.estabelecimento.status =
                    :status
              AND usuarioEstabelecimento.status = :status
              AND (
                    :idUsuarioEmpresa IS NULL
                    OR usuarioEstabelecimento.usuarioEmpresa.id =
                        :idUsuarioEmpresa
              )
            """)
    Page<UsuarioEstabelecimentoModel>
            buscarAtivosDaOrganizacao(
                    Pageable paginacao,

                    @Param("idOrganizacao")
                    Long idOrganizacao,

                    @Param("idUsuarioEmpresa")
                    Long idUsuarioEmpresa,

                    @Param("status")
                    StatusEnum status
            );

    boolean existsByUsuarioEmpresaAndEstabelecimentoAndStatus(
            UsuarioEmpresaModel usuarioEmpresa,
            EstabelecimentoModel estabelecimento,
            StatusEnum status
    );

    boolean existsByUsuarioEmpresaIdAndStatus(
            Long idUsuarioEmpresa,
            StatusEnum status
    );

    boolean existsByEstabelecimentoIdAndStatus(
            Long idEstabelecimento,
            StatusEnum status
    );

    @EntityGraph(
            attributePaths = {
                    "usuarioEmpresa.usuarioOrganizacao.usuario",
                    "usuarioEmpresa.empresa",
                    "estabelecimento"
            }
    )
    @Query("""
            SELECT usuarioEstabelecimento
            FROM UsuarioEstabelecimentoModel usuarioEstabelecimento
            WHERE usuarioEstabelecimento.id = :id
              AND usuarioEstabelecimento.usuarioEmpresa.usuarioOrganizacao.organizacao.id =
                    :idOrganizacao
              AND usuarioEstabelecimento.usuarioEmpresa.empresa.organizacao.id =
                    :idOrganizacao
              AND usuarioEstabelecimento.estabelecimento.empresa.organizacao.id =
                    :idOrganizacao
              AND usuarioEstabelecimento.estabelecimento.empresa =
                    usuarioEstabelecimento.usuarioEmpresa.empresa
              AND usuarioEstabelecimento.usuarioEmpresa.usuarioOrganizacao.status =
                    :status
              AND usuarioEstabelecimento.usuarioEmpresa.usuarioOrganizacao.usuario.status =
                    :status
              AND usuarioEstabelecimento.usuarioEmpresa.status =
                    :status
              AND usuarioEstabelecimento.usuarioEmpresa.empresa.status =
                    :status
              AND usuarioEstabelecimento.estabelecimento.status =
                    :status
              AND usuarioEstabelecimento.status = :status
            """)
    Optional<UsuarioEstabelecimentoModel>
            buscarAtivoDaOrganizacaoPorId(
                    @Param("id")
                    Long id,

                    @Param("idOrganizacao")
                    Long idOrganizacao,

                    @Param("status")
                    StatusEnum status
            );
}