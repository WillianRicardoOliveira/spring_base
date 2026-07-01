package com.empresa.erp.domain.acesso.permissao.record;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.old.StatusEnum;

public record ListaPermissaoRecord(
    Long id,
    String nome,
    String chave,
    String descricao,
    StatusEnum status
) {

    public ListaPermissaoRecord(PermissaoModel dados) {
        this(
            dados.getId(),
            dados.getNome(),
            dados.getChave(),
            dados.getDescricao(),
            dados.getStatus()
        );
    }
}