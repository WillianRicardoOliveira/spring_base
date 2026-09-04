package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.base.model.StatusEnum;

public record ListaUsuarioEmpresaRecord(
        Long id,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Boolean todosEstabelecimentos,
        StatusEnum status
) {

    public ListaUsuarioEmpresaRecord(
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
                usuarioEmpresa.getTodosEstabelecimentos(),
                usuarioEmpresa.getStatus()
        );
    }
}