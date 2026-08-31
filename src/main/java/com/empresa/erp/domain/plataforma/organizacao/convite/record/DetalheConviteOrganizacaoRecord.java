package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import java.time.LocalDateTime;

import com.empresa.erp.domain.base.record.AuditoriaRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;

public record DetalheConviteOrganizacaoRecord(
        Long id,
        String nomeOrganizacao,
        String emailAdministrador,
        LocalDateTime criadoEm,
        LocalDateTime expiraEm,
        LocalDateTime aceitoEm,
        StatusConviteOrganizacaoEnum status,
        boolean expirado,
        AuditoriaRecord auditoria
) {

    public DetalheConviteOrganizacaoRecord(
            Long id,
            String nomeOrganizacao,
            String emailAdministrador,
            LocalDateTime criadoEm,
            LocalDateTime expiraEm,
            LocalDateTime aceitoEm,
            StatusConviteOrganizacaoEnum status,
            boolean expirado
    ) {
        this(
                id,
                nomeOrganizacao,
                emailAdministrador,
                criadoEm,
                expiraEm,
                aceitoEm,
                status,
                expirado,
                null
        );
    }

    public DetalheConviteOrganizacaoRecord(
            ConviteOrganizacaoModel convite,
            LocalDateTime referencia
    ) {
        this(
                convite.getId(),
                convite.getNomeOrganizacao(),
                convite.getEmailAdministrador(),
                convite.getCriadoEm(),
                convite.getExpiraEm(),
                convite.getAceitoEm(),
                convite.getStatus(),
                convite.getStatus()
                        == StatusConviteOrganizacaoEnum.PENDENTE
                        && !convite.getExpiraEm()
                                .isAfter(referencia),
                new AuditoriaRecord(convite)
        );
    }
}