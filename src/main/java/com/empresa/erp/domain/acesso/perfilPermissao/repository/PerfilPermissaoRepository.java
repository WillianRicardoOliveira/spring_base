package com.empresa.erp.domain.acesso.perfilPermissao.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public interface PerfilPermissaoRepository extends JpaRepository<PerfilPermissaoModel, Long> {

    Page<PerfilPermissaoModel> findAllByPerfilIdAndStatus(Pageable paginacao, Long idPerfil, StatusEnum status);

    boolean existsByPerfilAndPermissaoAndStatus(PerfilModel perfil, PermissaoModel permissao, StatusEnum status);

    Optional<PerfilPermissaoModel> findByPerfilAndPermissaoAndStatus(PerfilModel perfil, PermissaoModel permissao, StatusEnum status);
    
}