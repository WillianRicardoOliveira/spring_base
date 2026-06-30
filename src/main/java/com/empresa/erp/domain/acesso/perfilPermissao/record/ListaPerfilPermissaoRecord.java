package com.empresa.erp.domain.acesso.perfilPermissao.record;

import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public record ListaPerfilPermissaoRecord(
    Long id,
    Long idPermissao,
    String permissao,
    String chave,
    StatusEnum status
) {

    public ListaPerfilPermissaoRecord(PerfilPermissaoModel dados) {
        this(
            dados.getId(),
            dados.getPermissao().getId(),
            dados.getPermissao().getNome(),
            dados.getPermissao().getChave(),
            dados.getStatus()
        );
    }
}