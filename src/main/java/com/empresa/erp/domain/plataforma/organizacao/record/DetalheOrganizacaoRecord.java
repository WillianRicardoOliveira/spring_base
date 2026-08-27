package com.empresa.erp.domain.plataforma.organizacao.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

public record DetalheOrganizacaoRecord(
        Long id,
        String nome,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheOrganizacaoRecord(
            Long id,
            String nome,
            StatusEnum status
    ) {
        this(
                id,
                nome,
                status,
                null
        );
    }

    public DetalheOrganizacaoRecord(
            OrganizacaoModel organizacao
    ) {
        this(
                organizacao.getId(),
                organizacao.getNome(),
                organizacao.getStatus(),
                new AuditoriaRecord(organizacao)
        );
    }
}