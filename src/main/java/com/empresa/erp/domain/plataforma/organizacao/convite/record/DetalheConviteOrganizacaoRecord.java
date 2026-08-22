package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import java.time.LocalDateTime;

import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;

public record DetalheConviteOrganizacaoRecord(
        Long id,
        String nomeOrganizacao,
        String emailAdministrador,
        LocalDateTime expiraEm,
        StatusConviteOrganizacaoEnum status
) {

    public DetalheConviteOrganizacaoRecord(
            ConviteOrganizacaoModel convite
    ) {
        this(
                convite.getId(),
                convite.getNomeOrganizacao(),
                convite.getEmailAdministrador(),
                convite.getExpiraEm(),
                convite.getStatus()
        );
    }
}