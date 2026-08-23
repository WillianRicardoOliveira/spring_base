package com.empresa.erp.domain.plataforma.organizacao.convite.event;

import java.time.LocalDateTime;

public record EnvioConviteOrganizacaoSolicitadoEvent(
        Long idConvite,
        String emailDestino,
        String nomeOrganizacao,
        String token,
        LocalDateTime expiraEm
) {

    @Override
    public String toString() {
        return "EnvioConviteOrganizacaoSolicitadoEvent["
                + "idConvite=" + idConvite
                + ", emailDestino=****"
                + ", nomeOrganizacao=****"
                + ", token=****"
                + ", expiraEm=" + expiraEm
                + "]";
    }
}