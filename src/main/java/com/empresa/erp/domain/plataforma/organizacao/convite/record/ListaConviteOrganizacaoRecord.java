package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import java.time.LocalDateTime;

import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;

public record ListaConviteOrganizacaoRecord(
        Long id,
        String nomeOrganizacao,
        String emailAdministrador,
        LocalDateTime criadoEm,
        LocalDateTime expiraEm,
        StatusConviteOrganizacaoEnum status,
        boolean expirado
) {

    public ListaConviteOrganizacaoRecord(
            ConviteOrganizacaoModel convite,
            LocalDateTime referencia
    ) {
        this(
                convite.getId(),
                convite.getNomeOrganizacao(),
                convite.getEmailAdministrador(),
                convite.getCriadoEm(),
                convite.getExpiraEm(),
                convite.getStatus(),
                convite.getStatus()
                        == StatusConviteOrganizacaoEnum.PENDENTE
                        && !convite.getExpiraEm()
                                .isAfter(referencia)
        );
    }
}