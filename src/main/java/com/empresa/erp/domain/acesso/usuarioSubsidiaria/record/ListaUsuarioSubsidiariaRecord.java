package com.empresa.erp.domain.acesso.usuarioSubsidiaria.record;

import com.empresa.erp.domain.acesso.usuarioSubsidiaria.model.UsuarioSubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;

public record ListaUsuarioSubsidiariaRecord(
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

    public ListaUsuarioSubsidiariaRecord(
            UsuarioSubsidiariaModel usuarioSubsidiaria
    ) {
        this(
                usuarioSubsidiaria.getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
                        .getUsuario()
                        .getId(),
                usuarioSubsidiaria
                        .getUsuarioEmpresa()
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
                usuarioSubsidiaria.getStatus()
        );
    }
}