package com.empresa.erp.domain.acesso.usuarioPerfil.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioPerfilRepository extends JpaRepository<UsuarioPerfilModel, Long> {

    List<UsuarioPerfilModel> findAllByUsuarioIdAndStatus(Long idUsuario, StatusEnum status);

    boolean existsByUsuarioAndPerfilAndStatus(UsuarioModel usuario, PerfilModel perfil, StatusEnum status);

    Optional<UsuarioPerfilModel> findByUsuarioAndPerfilAndStatus(UsuarioModel usuario, PerfilModel perfil, StatusEnum status);
    
    @Query("""
    	    SELECT DISTINCT permissao.chave
    	    FROM UsuarioPerfilModel usuarioPerfil
    	    JOIN PerfilPermissaoModel perfilPermissao
    	        ON perfilPermissao.perfil = usuarioPerfil.perfil
    	    JOIN perfilPermissao.permissao permissao
    	    WHERE usuarioPerfil.usuario.id = :idUsuario
    	      AND usuarioPerfil.status = :status
    	      AND perfilPermissao.status = :status
    	      AND usuarioPerfil.perfil.status = :status
    	      AND permissao.status = :status
    	""")
    	Set<String> buscarChavesPermissoesAtivasPorUsuario(
    	    @Param("idUsuario") Long idUsuario,
    	    @Param("status") StatusEnum status
    	);
    
}