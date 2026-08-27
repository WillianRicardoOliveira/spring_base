package com.empresa.erp.domain.usuario.record;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalheUsuarioRecord(
        Long id,
        String email,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheUsuarioRecord(
            Long id,
            String email,
            StatusEnum status
    ) {
        this(
                id,
                email,
                status,
                null
        );
    }

    public DetalheUsuarioRecord(
            UsuarioOrganizacaoModel usuarioOrganizacao
    ) {
        this(
                usuarioOrganizacao
                        .getUsuario()
                        .getId(),
                usuarioOrganizacao
                        .getUsuario()
                        .getEmail(),
                usuarioOrganizacao.getStatus(),
                new AuditoriaRecord(usuarioOrganizacao)
        );
    }
}