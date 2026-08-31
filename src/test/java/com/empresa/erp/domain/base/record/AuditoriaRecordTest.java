package com.empresa.erp.domain.base.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.base.model.AuditoriaModel;

class AuditoriaRecordTest {

    @Test
    @DisplayName("Deve criar record a partir de entidade auditavel")
    void deveCriarRecordAPartirDeEntidadeAuditavel() {
        var entidade = new EntidadeAuditavelTeste();

        var criadoEm = LocalDateTime.of(2026, 8, 27, 10, 0);
        var atualizadoEm = LocalDateTime.of(2026, 8, 27, 11, 0);
        var removidoEm = LocalDateTime.of(2026, 8, 27, 12, 0);

        ReflectionTestUtils.setField(entidade, "criadoEm", criadoEm);
        ReflectionTestUtils.setField(entidade, "criadoPor", 10L);
        ReflectionTestUtils.setField(entidade, "atualizadoEm", atualizadoEm);
        ReflectionTestUtils.setField(entidade, "atualizadoPor", 20L);
        ReflectionTestUtils.setField(entidade, "removidoEm", removidoEm);
        ReflectionTestUtils.setField(entidade, "removidoPor", 30L);

        var record = new AuditoriaRecord(entidade);

        assertThat(record.criadoEm()).isEqualTo(criadoEm);
        assertThat(record.criadoPor()).isEqualTo(10L);
        assertThat(record.atualizadoEm()).isEqualTo(atualizadoEm);
        assertThat(record.atualizadoPor()).isEqualTo(20L);
        assertThat(record.removidoEm()).isEqualTo(removidoEm);
        assertThat(record.removidoPor()).isEqualTo(30L);
    }

    private static class EntidadeAuditavelTeste extends AuditoriaModel {
    }
}