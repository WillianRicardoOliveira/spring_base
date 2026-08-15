package com.empresa.erp.domain.configuracao.subsidiaria.record;

import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.old.StatusEnum;

public record DetalheSubsidiariaRecord(
        Long id,
        Long idEmpresa,
        String empresa,
        String nome,
        StatusEnum status
) {

    public DetalheSubsidiariaRecord(
            SubsidiariaModel subsidiaria
    ) {
        this(
                subsidiaria.getId(),
                subsidiaria.getEmpresa().getId(),
                subsidiaria.getEmpresa().getNome(),
                subsidiaria.getNome(),
                subsidiaria.getStatus()
        );
    }
}