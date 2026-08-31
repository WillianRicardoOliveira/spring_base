package com.empresa.erp.domain.acesso.usuarioPerfil.record;

import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalheUsuarioPerfilRecord(
        Long id,
        Long idUsuario,
        String usuario,
        Long idPerfil,
        String perfil,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheUsuarioPerfilRecord(
            Long id,
            Long idUsuario,
            String usuario,
            Long idPerfil,
            String perfil,
            StatusEnum status
    ) {
        this(
                id,
                idUsuario,
                usuario,
                idPerfil,
                perfil,
                status,
                null
        );
    }

    public DetalheUsuarioPerfilRecord(
            UsuarioPerfilModel dados
    ) {
        this(
                dados.getId(),
                dados.getUsuarioOrganizacao()
                        .getUsuario()
                        .getId(),
                dados.getUsuarioOrganizacao()
                        .getUsuario()
                        .getEmail(),
                dados.getPerfil().getId(),
                dados.getPerfil().getNome(),
                dados.getStatus(),
                new AuditoriaRecord(dados)
        );
    }
}