package com.empresa.erp.domain.acesso.usuarioPerfil.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioPerfilRepository extends JpaRepository<UsuarioPerfilModel, Long> {

    List<UsuarioPerfilModel> findAllByUsuarioIdAndStatus(Long idUsuario, StatusEnum status);

    boolean existsByUsuarioAndPerfilAndStatus(UsuarioModel usuario, PerfilModel perfil, StatusEnum status);

    Optional<UsuarioPerfilModel> findByUsuarioAndPerfilAndStatus(UsuarioModel usuario, PerfilModel perfil, StatusEnum status);
    
}