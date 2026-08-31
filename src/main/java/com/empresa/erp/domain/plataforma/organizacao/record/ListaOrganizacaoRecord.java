package com.empresa.erp.domain.plataforma.organizacao.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

public record ListaOrganizacaoRecord(
        Long id,
        String nome,
        StatusEnum status
) {

    public ListaOrganizacaoRecord(
            OrganizacaoModel organizacao
    ) {
        this(
                organizacao.getId(),
                organizacao.getNome(),
                organizacao.getStatus()
        );
    }
}