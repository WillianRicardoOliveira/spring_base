package com.empresa.erp.domain.configuracao.empresa.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;

public record ListaEmpresaRecord(
        Long id,
        String nome,
        StatusEnum status
) {

    public ListaEmpresaRecord(EmpresaModel empresa) {
        this(
                empresa.getId(),
                empresa.getNome(),
                empresa.getStatus()
        );
    }
}