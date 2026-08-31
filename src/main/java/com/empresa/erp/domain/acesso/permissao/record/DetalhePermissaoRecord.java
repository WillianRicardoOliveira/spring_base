package com.empresa.erp.domain.acesso.permissao.record;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalhePermissaoRecord(
        Long id,
        String nome,
        String chave,
        String descricao,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalhePermissaoRecord(
            Long id,
            String nome,
            String chave,
            String descricao,
            StatusEnum status
    ) {
        this(
                id,
                nome,
                chave,
                descricao,
                status,
                null
        );
    }

    public DetalhePermissaoRecord(PermissaoModel dados) {
        this(
                dados.getId(),
                dados.getNome(),
                dados.getChave(),
                dados.getDescricao(),
                dados.getStatus(),
                new AuditoriaRecord(dados)
        );
    }
}