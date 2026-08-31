package com.empresa.erp.domain.plataforma.organizacao.convite.port;

import java.time.LocalDateTime;

public interface EnvioConviteOrganizacaoPort {

    void enviar(
            String emailDestino,
            String nomeOrganizacao,
            String token,
            LocalDateTime expiraEm
    );
}