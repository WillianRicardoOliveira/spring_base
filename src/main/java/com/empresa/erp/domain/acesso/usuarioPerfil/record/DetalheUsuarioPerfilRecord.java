package com.empresa.erp.domain.acesso.usuarioPerfil.record;

import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.old.StatusEnum;

public record DetalheUsuarioPerfilRecord(
    Long id,
    Long idUsuario,
    String usuario,
    Long idPerfil,
    String perfil,
    StatusEnum status
) {

    public DetalheUsuarioPerfilRecord(UsuarioPerfilModel dados) {
        this(
            dados.getId(),
            dados.getUsuario().getId(),
            dados.getUsuario().getEmail(),
            dados.getPerfil().getId(),
            dados.getPerfil().getNome(),
            dados.getStatus()
        );
    }
}