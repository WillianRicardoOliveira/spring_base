package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.old.StatusEnum;

public record ListaUsuarioEmpresaRecord(
        Long id,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Boolean todasSubsidiarias,
        StatusEnum status
) {

    public ListaUsuarioEmpresaRecord(
            UsuarioEmpresaModel usuarioEmpresa
    ) {
        this(
                usuarioEmpresa.getId(),
                usuarioEmpresa.getUsuario().getId(),
                usuarioEmpresa.getUsuario().getEmail(),
                usuarioEmpresa.getEmpresa().getId(),
                usuarioEmpresa.getEmpresa().getNome(),
                usuarioEmpresa.getTodasSubsidiarias(),
                usuarioEmpresa.getStatus()
        );
    }
}