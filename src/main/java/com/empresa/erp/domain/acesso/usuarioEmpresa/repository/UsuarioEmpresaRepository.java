package com.empresa.erp.domain.acesso.usuarioEmpresa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public interface UsuarioEmpresaRepository
        extends JpaRepository<UsuarioEmpresaModel, Long> {

    Page<UsuarioEmpresaModel>
            findAllByEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<UsuarioEmpresaModel>
            findAllByUsuarioIdAndEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idUsuario,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<UsuarioEmpresaModel>
            findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idEmpresa,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<UsuarioEmpresaModel>
            findAllByUsuarioIdAndEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idUsuario,
                    Long idEmpresa,
                    Long idOrganizacao,
                    StatusEnum status
            );

    boolean existsByUsuarioAndEmpresaAndStatus(
            UsuarioModel usuario,
            EmpresaModel empresa,
            StatusEnum status
    );

    Optional<UsuarioEmpresaModel>
            findByIdAndEmpresaOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacao,
                    StatusEnum status
            );

    boolean existsByUsuarioIdAndStatus(
            Long idUsuario,
            StatusEnum status
    );

    boolean existsByEmpresaIdAndStatus(
            Long idEmpresa,
            StatusEnum status
    );
}