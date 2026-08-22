package com.empresa.erp.domain.plataforma.organizacao.record;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

public record DetalheOrganizacaoRecord(
        Long id,
        String nome,
        StatusEnum status
) {

    public DetalheOrganizacaoRecord(
            OrganizacaoModel organizacao
    ) {
        this(
                organizacao.getId(),
                organizacao.getNome(),
                organizacao.getStatus()
        );
    }
}