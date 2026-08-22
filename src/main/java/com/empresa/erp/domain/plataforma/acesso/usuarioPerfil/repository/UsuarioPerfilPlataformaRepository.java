package com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.model.UsuarioPerfilPlataformaModel;

public interface UsuarioPerfilPlataformaRepository
        extends JpaRepository<
                UsuarioPerfilPlataformaModel,
                Long
        > {

    @Query("""
            SELECT DISTINCT permissao.chave
            FROM UsuarioPerfilPlataformaModel usuarioPerfil
            JOIN PerfilPlataformaPermissaoModel perfilPermissao
                ON perfilPermissao.perfil = usuarioPerfil.perfil
            JOIN perfilPermissao.permissao permissao
            WHERE usuarioPerfil.usuario.id = :idUsuario
              AND usuarioPerfil.usuario.status = :status
              AND usuarioPerfil.status = :status
              AND usuarioPerfil.perfil.status = :status
              AND perfilPermissao.status = :status
              AND permissao.escopo = :escopo
              AND permissao.status = :status
            """)
    Set<String> buscarChavesPermissoesAtivasPorUsuario(
            @Param("idUsuario")
            Long idUsuario,

            @Param("escopo")
            EscopoPermissaoEnum escopo,

            @Param("status")
            StatusEnum status
    );
}