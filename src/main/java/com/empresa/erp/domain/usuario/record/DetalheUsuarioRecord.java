package com.empresa.erp.domain.usuario.record;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.old.StatusEnum;

public record DetalheUsuarioRecord(
        Long id,
        String email,
        StatusEnum status
) {

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
                usuarioOrganizacao.getStatus()
        );
    }
}