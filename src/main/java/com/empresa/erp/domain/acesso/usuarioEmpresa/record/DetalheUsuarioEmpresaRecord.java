package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.old.StatusEnum;

public record DetalheUsuarioEmpresaRecord(
        Long id,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Boolean todasSubsidiarias,
        StatusEnum status
) {

    public DetalheUsuarioEmpresaRecord(
            UsuarioEmpresaModel usuarioEmpresa
    ) {
        this(
                usuarioEmpresa.getId(),
                usuarioEmpresa
                        .getUsuarioOrganizacao()
                        .getUsuario()
                        .getId(),
                usuarioEmpresa
                        .getUsuarioOrganizacao()
                        .getUsuario()
                        .getEmail(),
                usuarioEmpresa.getEmpresa().getId(),
                usuarioEmpresa.getEmpresa().getNome(),
                usuarioEmpresa.getTodasSubsidiarias(),
                usuarioEmpresa.getStatus()
        );
    }
}