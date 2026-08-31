package com.empresa.erp.domain.base.record;

import java.time.LocalDateTime;

import com.empresa.erp.domain.base.model.AuditoriaModel;

public record AuditoriaRecord(
        LocalDateTime criadoEm,
        Long criadoPor,
        LocalDateTime atualizadoEm,
        Long atualizadoPor,
        LocalDateTime removidoEm,
        Long removidoPor
) {

    public AuditoriaRecord(AuditoriaModel auditoria) {
        this(
                auditoria.getCriadoEm(),
                auditoria.getCriadoPor(),
                auditoria.getAtualizadoEm(),
                auditoria.getAtualizadoPor(),
                auditoria.getRemovidoEm(),
                auditoria.getRemovidoPor()
        );
    }
}