package com.empresa.erp.domain.configuracao.subsidiaria.record;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.base.record.AuditoriaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;

public record DetalheSubsidiariaRecord(
        Long id,
        Long idEmpresa,
        String empresa,
        String nome,
        StatusEnum status,
        AuditoriaRecord auditoria
) {

    public DetalheSubsidiariaRecord(
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

    public DetalheSubsidiariaRecord(
            SubsidiariaModel subsidiaria
    ) {
        this(
                subsidiaria.getId(),
                subsidiaria.getEmpresa().getId(),
                subsidiaria.getEmpresa().getNome(),
                subsidiaria.getNome(),
                subsidiaria.getStatus(),
                new AuditoriaRecord(subsidiaria)
        );
    }
}