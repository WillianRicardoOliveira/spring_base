package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

public record ResultadoAceiteConviteOrganizacaoRecord(
        Long idOrganizacao,
        String nomeOrganizacao
) {

    public ResultadoAceiteConviteOrganizacaoRecord(
            OrganizacaoModel organizacao
    ) {
        this(
                organizacao.getId(),
                organizacao.getNome()
        );
    }
}