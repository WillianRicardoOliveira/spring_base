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

    Page<SubsidiariaModel>
            findAllByEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<SubsidiariaModel>
            findByEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    String filtro,
                    StatusEnum status
            );

    Page<SubsidiariaModel>
            findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idEmpresa,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<SubsidiariaModel>
            findByEmpresaIdAndEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    Long idEmpresa,
                    Long idOrganizacao,
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

    Optional<SubsidiariaModel>
            findByIdAndEmpresaOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacao,
                    StatusEnum status
            );

    /*
     * Temporariamente mantido para UsuarioSubsidiariaService.
     * Deve ser removido quando os acessos por subsidiária
     * forem isolados pela organização.
     */
    Optional<SubsidiariaModel> findByIdAndStatus(
            Long id,
            StatusEnum status
    );
}