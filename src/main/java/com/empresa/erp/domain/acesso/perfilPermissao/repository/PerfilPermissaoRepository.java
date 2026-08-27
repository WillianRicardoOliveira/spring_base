package com.empresa.erp.domain.acesso.perfilPermissao.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.base.model.StatusEnum;

public interface PerfilPermissaoRepository
        extends JpaRepository<PerfilPermissaoModel, Long> {

    Page<PerfilPermissaoModel>
            findAllByPerfilIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                    Pageable paginacao,
                    Long idPerfil,
                    Long idOrganizacao,
                    EscopoPermissaoEnum escopo,
                    StatusEnum statusPermissao,
                    StatusEnum statusVinculo
            );

    Optional<PerfilPermissaoModel>
            findByPerfilIdAndPermissaoIdAndPerfilOrganizacaoId(
                    Long idPerfil,
                    Long idPermissao,
                    Long idOrganizacao
            );

    boolean
            existsByPerfilIdAndPerfilOrganizacaoIdAndStatus(
                    Long idPerfil,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Optional<PerfilPermissaoModel>
            findByIdAndPerfilOrganizacaoIdAndPermissaoEscopoAndPermissaoStatusAndStatus(
                    Long id,
                    Long idOrganizacao,
                    EscopoPermissaoEnum escopo,
                    StatusEnum statusPermissao,
                    StatusEnum statusVinculo
            );
}