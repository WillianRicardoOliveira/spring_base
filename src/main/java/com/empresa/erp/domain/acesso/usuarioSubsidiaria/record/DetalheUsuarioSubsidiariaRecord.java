package com.empresa.erp.domain.acesso.usuarioSubsidiaria.record;

import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalheUsuarioSubsidiariaRecord(
        Long id,
        Long idUsuarioEmpresa,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Long idSubsidiaria,
        String subsidiaria,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheUsuarioSubsidiariaRecord(
            Long id,
            Long idUsuarioEmpresa,
            Long idUsuario,
            String usuario,
            Long idEmpresa,
            String empresa,
            Long idSubsidiaria,
            String subsidiaria,
            StatusEnum status
    ) {
        this(
                id,
                idUsuarioEmpresa,
                idUsuario,
                usuario,
                idEmpresa,
                empresa,
                idSubsidiaria,
                subsidiaria,
                status,
                null
        );
    }

    public DetalheUsuarioSubsidiariaRecord(
            UsuarioSubsidiariaModel usuarioSubsidiaria
    ) {
        this(
                usuarioSubsidiaria.getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getUsuarioOrganizacao()
                        .getUsuario()
                        .getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getUsuarioOrganizacao()
                        .getUsuario()
                        .getEmail(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getEmpresa()
                        .getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getEmpresa()
                        .getNome(),
                usuarioSubsidiaria
                        .getSubsidiaria()
                        .getId(),
                usuarioSubsidiaria
                        .getSubsidiaria()
                        .getNome(),
                usuarioSubsidiaria.getStatus(),
                new AuditoriaRecord(usuarioSubsidiaria)
        );
    }
}