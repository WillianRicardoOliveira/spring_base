package com.empresa.erp.domain.usuario.record;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.old.StatusEnum;

public record ListaUsuarioRecord(
        Long id,
        String email,
        StatusEnum status
) {

    public ListaUsuarioRecord(
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