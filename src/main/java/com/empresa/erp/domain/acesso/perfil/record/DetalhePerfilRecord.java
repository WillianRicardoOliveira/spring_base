package com.empresa.erp.domain.acesso.perfil.record;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;

public record DetalhePerfilRecord(
        Long id,
        String nome,
        String descricao,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalhePerfilRecord(
            Long id,
            String nome,
            String descricao,
            StatusEnum status
    ) {
        this(
                id,
                nome,
                descricao,
                status,
                null
        );
    }

    public DetalhePerfilRecord(PerfilModel dados) {
        this(
                dados.getId(),
                dados.getNome(),
                dados.getDescricao(),
                dados.getStatus(),
                new AuditoriaRecord(dados)
        );
    }
}