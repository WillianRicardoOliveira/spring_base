package com.empresa.erp.domain.acesso.perfilPermissao.record;

import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.old.StatusEnum;

public record DetalhePerfilPermissaoRecord(
    Long id,
    Long idPerfil,
    String perfil,
    Long idPermissao,
    String permissao,
    String chave,
    StatusEnum status
) {

    public DetalhePerfilPermissaoRecord(PerfilPermissaoModel dados) {
        this(
            dados.getId(),
            dados.getPerfil().getId(),
            dados.getPerfil().getNome(),
            dados.getPermissao().getId(),
            dados.getPermissao().getNome(),
            dados.getPermissao().getChave(),
            dados.getStatus()
        );
    }
}