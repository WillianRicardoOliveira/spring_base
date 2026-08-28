package com.empresa.erp.domain.configuracao.empresa.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;

public record DetalheEmpresaRecord(
        Long id,
        String nome,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheEmpresaRecord(
            Long id,
            String nome,
            StatusEnum status
    ) {
        this(
                id,
                nome,
                status,
                null
        );
    }

    public DetalheEmpresaRecord(EmpresaModel empresa) {
        this(
                empresa.getId(),
                empresa.getNome(),
                empresa.getStatus(),
                new AuditoriaRecord(empresa)
        );
    }
}