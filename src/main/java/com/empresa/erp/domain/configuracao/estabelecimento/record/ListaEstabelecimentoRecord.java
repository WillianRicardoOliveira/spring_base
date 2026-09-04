package com.empresa.erp.domain.configuracao.estabelecimento.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;

public record ListaEstabelecimentoRecord(
        Long id,
        Long idEmpresa,
        String empresa,
        String nome,
        StatusEnum status
) {

    public ListaEstabelecimentoRecord(
            EstabelecimentoModel estabelecimento
    ) {
        this(
        		estabelecimento.getId(),
        		estabelecimento.getEmpresa().getId(),
        		estabelecimento.getEmpresa().getNome(),
        		estabelecimento.getNome(),
        		estabelecimento.getStatus()
        );
    }
}