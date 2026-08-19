package com.empresa.erp.domain.configuracao.empresa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.old.StatusEnum;

public interface EmpresaRepository
        extends JpaRepository<EmpresaModel, Long> {

    Page<EmpresaModel>
            findByOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    String filtro,
                    StatusEnum status
            );

    Page<EmpresaModel> findAllByOrganizacaoIdAndStatus(
            Pageable paginacao,
            Long idOrganizacao,
            StatusEnum status
    );

    boolean existsByOrganizacaoIdAndNomeIgnoreCaseAndStatus(
            Long idOrganizacao,
            String nome,
            StatusEnum status
    );

    boolean
            existsByOrganizacaoIdAndNomeIgnoreCaseAndStatusAndIdNot(
                    Long idOrganizacao,
                    String nome,
                    StatusEnum status,
                    Long id
            );

    Optional<EmpresaModel>
            findByIdAndOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacao,
                    StatusEnum status
            );

    /*
     * Métodos temporariamente mantidos para os fluxos de
     * Subsidiária e UsuarioEmpresa.
     *
     * Devem ser removidos após esses módulos passarem a
     * respeitar o contexto da organização.
     */

    Page<EmpresaModel> findAllByStatus(
            Pageable paginacao,
            StatusEnum status
    );

    Optional<EmpresaModel> findByIdAndStatus(
            Long id,
            StatusEnum status
    );
}