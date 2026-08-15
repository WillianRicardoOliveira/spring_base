package com.empresa.erp.domain.configuracao.subsidiaria.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface SubsidiariaRepository
        extends JpaRepository<SubsidiariaModel, Long> {

    Page<SubsidiariaModel> findAllByStatus(
            Pageable paginacao,
            StatusEnum status
    );

    Page<SubsidiariaModel>
            findByNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    String filtro,
                    StatusEnum status
            );

    Page<SubsidiariaModel> findAllByEmpresaIdAndStatus(
            Pageable paginacao,
            Long idEmpresa,
            StatusEnum status
    );

    Page<SubsidiariaModel>
            findByEmpresaIdAndNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    Long idEmpresa,
                    String filtro,
                    StatusEnum status
            );

    boolean existsByEmpresaAndNomeIgnoreCaseAndStatus(
            EmpresaModel empresa,
            String nome,
            StatusEnum status
    );

    boolean
            existsByEmpresaAndNomeIgnoreCaseAndStatusAndIdNot(
                    EmpresaModel empresa,
                    String nome,
                    StatusEnum status,
                    Long id
            );

    boolean existsByEmpresaIdAndStatus(
            Long idEmpresa,
            StatusEnum status
    );

    Optional<SubsidiariaModel> findByIdAndStatus(
            Long id,
            StatusEnum status
    );
}