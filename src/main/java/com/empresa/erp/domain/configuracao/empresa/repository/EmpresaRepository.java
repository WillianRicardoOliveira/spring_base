package com.empresa.erp.domain.configuracao.empresa.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;

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
}