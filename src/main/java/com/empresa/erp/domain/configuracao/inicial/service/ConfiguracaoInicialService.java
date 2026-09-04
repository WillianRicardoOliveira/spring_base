package com.empresa.erp.domain.configuracao.inicial.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.organizacao.contexto.ContextoOrganizacao;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.repository.EmpresaRepository;
import com.empresa.erp.domain.configuracao.inicial.model.ProximaEtapaConfiguracaoEnum;
import com.empresa.erp.domain.configuracao.inicial.record.EstadoConfiguracaoInicialRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracaoInicialService {

    private final EmpresaRepository
            empresaRepository;

    private final ContextoOrganizacao
            contextoOrganizacao;

    @Transactional(readOnly = true)
    public EstadoConfiguracaoInicialRecord
            consultar() {

        Long idOrganizacao =
                contextoOrganizacao.getIdOrganizacao();

        boolean empresaCadastrada =
                empresaRepository
                        .existsByOrganizacaoIdAndStatus(
                                idOrganizacao,
                                StatusEnum.ATIVO
                        );

        return new EstadoConfiguracaoInicialRecord(
                empresaCadastrada,
                empresaCadastrada
                        ? null
                        : ProximaEtapaConfiguracaoEnum
                                .EMPRESA
        );
    }
}