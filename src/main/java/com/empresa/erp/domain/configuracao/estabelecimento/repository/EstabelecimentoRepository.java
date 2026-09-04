package com.empresa.erp.domain.configuracao.estabelecimento.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;

public interface EstabelecimentoRepository
        extends JpaRepository<EstabelecimentoModel, Long> {

    Page<EstabelecimentoModel>
            findAllByEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<EstabelecimentoModel>
            findByEmpresaOrganizacaoIdAndNomeContainingIgnoreCaseAndStatus(
                    Pageable paginacao,
                    Long idOrganizacao,
                    String filtro,
                    StatusEnum status
            );

    Page<EstabelecimentoModel>
            findAllByEmpresaIdAndEmpresaOrganizacaoIdAndStatus(
                    Pageable paginacao,
                    Long idEmpresa,
                    Long idOrganizacao,
                    StatusEnum status
            );

    Page<EstabelecimentoModel>
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

    Optional<EstabelecimentoModel>
            findByIdAndEmpresaOrganizacaoIdAndStatus(
                    Long id,
                    Long idOrganizacao,
                    StatusEnum status
            );
}