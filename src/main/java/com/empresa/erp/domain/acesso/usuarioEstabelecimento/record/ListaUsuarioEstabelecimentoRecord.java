package com.empresa.erp.domain.acesso.usuarioEstabelecimento.record;

import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.base.model.StatusEnum;

public record ListaUsuarioEstabelecimentoRecord(
        Long id,
        Long idUsuarioEmpresa,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Long idEstabelecimento,
        String estabelecimento,
        StatusEnum status
) {

    public ListaUsuarioEstabelecimentoRecord(
            UsuarioEstabelecimentoModel usuarioEstabelecimento
    ) {
        this(
                usuarioEstabelecimento.getId(),
                usuarioEstabelecimento
                        .getUsuarioEmpresa()
                        .getId(),
                usuarioEstabelecimento
                        .getUsuarioEmpresa()
                        .getUsuarioOrganizacao()
                        .getUsuario()
                        .getId(),
                usuarioEstabelecimento
                        .getUsuarioEmpresa()
                        .getUsuarioOrganizacao()
                        .getUsuario()
                        .getEmail(),
                usuarioEstabelecimento
                        .getUsuarioEmpresa()
                        .getEmpresa()
                        .getId(),
                usuarioEstabelecimento
                        .getUsuarioEmpresa()
                        .getEmpresa()
                        .getNome(),
                usuarioEstabelecimento
                        .getEstabelecimento()
                        .getId(),
                usuarioEstabelecimento
                        .getEstabelecimento()
                        .getNome(),
                usuarioEstabelecimento.getStatus()
        );
    }
}