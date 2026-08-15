package com.empresa.erp.domain.acesso.usuarioSubsidiaria.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface UsuarioSubsidiariaRepository
        extends JpaRepository<UsuarioSubsidiariaModel, Long> {

    Page<UsuarioSubsidiariaModel> findAllByStatus(
            Pageable paginacao,
            StatusEnum status
    );

    Page<UsuarioSubsidiariaModel>
            findAllByUsuarioEmpresaIdAndStatus(
                    Pageable paginacao,
                    Long idUsuarioEmpresa,
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

    Optional<UsuarioSubsidiariaModel> findByIdAndStatus(
            Long id,
            StatusEnum status
    );
}