package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalheUsuarioEmpresaRecord(
        Long id,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Boolean todasSubsidiarias,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheUsuarioEmpresaRecord(
            Long id,
            Long idUsuario,
            String usuario,
            Long idEmpresa,
            String empresa,
            Boolean todasSubsidiarias,
            StatusEnum status
    ) {
        this(
                id,
                idUsuario,
                usuario,
                idEmpresa,
                empresa,
                todasSubsidiarias,
                status,
                null
        );
    }

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
                usuarioEmpresa.getStatus(),
                new AuditoriaRecord(usuarioEmpresa)
        );
    }
}