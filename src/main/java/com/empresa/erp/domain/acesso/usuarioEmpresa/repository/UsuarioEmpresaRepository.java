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

    Page<UsuarioEmpresaModel> findAllByStatus(
            Pageable paginacao,
            StatusEnum status
    );

    Page<UsuarioEmpresaModel> findAllByUsuarioIdAndStatus(
            Pageable paginacao,
            Long idUsuario,
            StatusEnum status
    );

    Page<UsuarioEmpresaModel> findAllByEmpresaIdAndStatus(
            Pageable paginacao,
            Long idEmpresa,
            StatusEnum status
    );

    Page<UsuarioEmpresaModel>
            findAllByUsuarioIdAndEmpresaIdAndStatus(
                    Pageable paginacao,
                    Long idUsuario,
                    Long idEmpresa,
                    StatusEnum status
            );

    boolean existsByUsuarioAndEmpresaAndStatus(
            UsuarioModel usuario,
            EmpresaModel empresa,
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

    Optional<UsuarioEmpresaModel> findByIdAndStatus(
            Long id,
            StatusEnum status
    );

    Optional<UsuarioEmpresaModel>
            findByUsuarioIdAndEmpresaIdAndStatus(
                    Long idUsuario,
                    Long idEmpresa,
                    StatusEnum status
            );
}