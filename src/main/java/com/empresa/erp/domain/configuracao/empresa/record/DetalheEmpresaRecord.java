package com.empresa.erp.domain.configuracao.empresa.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;

public record DetalheEmpresaRecord(
        Long id,
        String nome,
        StatusEnum status
) {

    public DetalheEmpresaRecord(EmpresaModel empresa) {
        this(
                empresa.getId(),
                empresa.getNome(),
                empresa.getStatus()
        );
    }
}