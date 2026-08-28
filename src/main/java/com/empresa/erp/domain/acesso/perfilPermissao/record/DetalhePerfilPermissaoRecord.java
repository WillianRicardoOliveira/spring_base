package com.empresa.erp.domain.acesso.perfilPermissao.record;

import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalhePerfilPermissaoRecord(
        Long id,
        Long idPerfil,
        String perfil,
        Long idPermissao,
        String permissao,
        String chave,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalhePerfilPermissaoRecord(
            Long id,
            Long idPerfil,
            String perfil,
            Long idPermissao,
            String permissao,
            String chave,
            StatusEnum status
    ) {
        this(
                id,
                idPerfil,
                perfil,
                idPermissao,
                permissao,
                chave,
                status,
                null
        );
    }

    public DetalhePerfilPermissaoRecord(
            PerfilPermissaoModel dados
    ) {
        this(
                dados.getId(),
                dados.getPerfil().getId(),
                dados.getPerfil().getNome(),
                dados.getPermissao().getId(),
                dados.getPermissao().getNome(),
                dados.getPermissao().getChave(),
                dados.getStatus(),
                new AuditoriaRecord(dados)
        );
    }
}