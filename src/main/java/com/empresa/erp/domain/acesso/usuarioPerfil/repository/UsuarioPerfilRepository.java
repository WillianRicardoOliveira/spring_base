package com.empresa.erp.domain.acesso.usuarioPerfil.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.perfil.model.TipoPerfilSistemaEnum;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.base.model.StatusEnum;

public interface UsuarioPerfilRepository
        extends JpaRepository<UsuarioPerfilModel, Long> {

    @EntityGraph(attributePaths = "perfil")
    List<UsuarioPerfilModel>
            findAllByUsuarioOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                    Long idUsuarioOrganizacao,
                    Long idOrganizacao,
                    StatusEnum status
            );

    boolean
            existsByUsuarioOrganizacaoIdAndPerfilIdAndPerfilOrganizacaoIdAndStatus(
                    Long idUsuarioOrganizacao,
                    Long idPerfil,
                    Long idOrganizacao,
                    StatusEnum status
            );

    @EntityGraph(
            attributePaths = {
                    "usuarioOrganizacao.usuario",
                    "perfil"
            }
    )
    Optional<UsuarioPerfilModel>
            findByIdAndUsuarioOrganizacaoOrganizacaoIdAndPerfilOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacaoUsuario,
                    Long idOrganizacaoPerfil,
                    StatusEnum status
            );

    boolean
            existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                    Long idPerfil,
                    Long idOrganizacao,
                    StatusEnum status
            );

    @Query("""
            SELECT CASE
                    WHEN COUNT(usuarioPerfil) > 0
                    THEN true
                    ELSE false
                   END
            FROM UsuarioPerfilModel usuarioPerfil
            WHERE usuarioPerfil.usuarioOrganizacao.id =
                    :idUsuarioOrganizacao
              AND usuarioPerfil.perfil.tipoSistema =
                    :tipoPerfilSistema
              AND usuarioPerfil.usuarioOrganizacao.status =
                    :status
              AND usuarioPerfil.usuarioOrganizacao.usuario.status =
                    :status
              AND usuarioPerfil.perfil.status = :status
              AND usuarioPerfil.status = :status
            """)
    boolean possuiPerfilAdministradorAtivo(
            @Param("idUsuarioOrganizacao")
            Long idUsuarioOrganizacao,

            @Param("tipoPerfilSistema")
            TipoPerfilSistemaEnum tipoPerfilSistema,

            @Param("status")
            StatusEnum status
    );

    @Query("""
            SELECT CASE
                    WHEN COUNT(usuarioPerfil) > 0
                    THEN true
                    ELSE false
                   END
            FROM UsuarioPerfilModel usuarioPerfil
            WHERE usuarioPerfil.usuarioOrganizacao.organizacao.id =
                    :idOrganizacao
              AND usuarioPerfil.usuarioOrganizacao.id <>
                    :idUsuarioOrganizacaoIgnorado
              AND usuarioPerfil.perfil.organizacao.id =
                    :idOrganizacao
              AND usuarioPerfil.perfil.tipoSistema =
                    :tipoPerfilSistema
              AND usuarioPerfil.usuarioOrganizacao.status =
                    :status
              AND usuarioPerfil.usuarioOrganizacao.usuario.status =
                    :status
              AND usuarioPerfil.perfil.status = :status
              AND usuarioPerfil.status = :status
            """)
    boolean existeOutroAdministradorAtivo(
            @Param("idOrganizacao")
            Long idOrganizacao,

            @Param("idUsuarioOrganizacaoIgnorado")
            Long idUsuarioOrganizacaoIgnorado,

            @Param("tipoPerfilSistema")
            TipoPerfilSistemaEnum tipoPerfilSistema,

            @Param("status")
            StatusEnum status
    );

    @Query("""
            SELECT DISTINCT permissao.chave
            FROM UsuarioPerfilModel usuarioPerfil
            JOIN PerfilPermissaoModel perfilPermissao
                ON perfilPermissao.perfil = usuarioPerfil.perfil
            JOIN perfilPermissao.permissao permissao
            WHERE usuarioPerfil.usuarioOrganizacao.usuario.id =
                    :idUsuario
              AND usuarioPerfil.usuarioOrganizacao.organizacao.id =
                    :idOrganizacao
              AND usuarioPerfil.perfil.organizacao.id =
                    :idOrganizacao
              AND permissao.escopo = :escopo
              AND usuarioPerfil.usuarioOrganizacao.status =
                    :status
              AND usuarioPerfil.usuarioOrganizacao.usuario.status =
                    :status
              AND usuarioPerfil.status = :status
              AND perfilPermissao.status = :status
              AND usuarioPerfil.perfil.status = :status
              AND permissao.status = :status
            """)
    Set<String>
            buscarChavesPermissoesAtivasPorUsuarioEOrganizacao(
                    @Param("idUsuario")
                    Long idUsuario,

                    @Param("idOrganizacao")
                    Long idOrganizacao,

                    @Param("escopo")
                    EscopoPermissaoEnum escopo,

                    @Param("status")
                    StatusEnum status
            );
}