package com.empresa.erp.domain.acesso.usuarioPerfil.record;

import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.old.StatusEnum;

public record ListaUsuarioPerfilRecord(
    Long id,
    Long idPerfil,
    String perfil,
    StatusEnum status
) {

    public ListaUsuarioPerfilRecord(UsuarioPerfilModel dados) {
        this(
            dados.getId(),
            dados.getPerfil().getId(),
            dados.getPerfil().getNome(),
            dados.getStatus()
        );
    }
}