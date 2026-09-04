package com.empresa.erp.domain.configuracao.estabelecimento.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;

public record DetalheEstabelecimentoRecord(
        Long id,
        Long idEmpresa,
        String empresa,
        String nome,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheEstabelecimentoRecord(
            Long id,
            Long idEmpresa,
            String empresa,
            String nome,
            StatusEnum status
    ) {
        this(
                id,
                idEmpresa,
                empresa,
                nome,
                status,
                null
        );
    }

    public DetalheEstabelecimentoRecord(
            EstabelecimentoModel estabelecimento
    ) {
        this(
        		estabelecimento.getId(),
        		estabelecimento.getEmpresa().getId(),
        		estabelecimento.getEmpresa().getNome(),
        		estabelecimento.getNome(),
        		estabelecimento.getStatus(),
                new AuditoriaRecord(estabelecimento)
        );
    }
}