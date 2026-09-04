package com.empresa.erp.domain.acesso.usuarioEstabelecimento.record;

import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalheUsuarioEstabelecimentoRecord(
        Long id,
        Long idUsuarioEmpresa,
        Long idUsuario,
        String usuario,
        Long idEmpresa,
        String empresa,
        Long idEstabelecimento,
        String estabelecimento,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheUsuarioEstabelecimentoRecord(
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
        this(
                id,
                idUsuarioEmpresa,
                idUsuario,
                usuario,
                idEmpresa,
                empresa,
                idEstabelecimento,
                estabelecimento,
                status,
                null
        );
    }

    public DetalheUsuarioEstabelecimentoRecord(
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
                usuarioEstabelecimento.getStatus(),
                new AuditoriaRecord(usuarioEstabelecimento)
        );
    }
}