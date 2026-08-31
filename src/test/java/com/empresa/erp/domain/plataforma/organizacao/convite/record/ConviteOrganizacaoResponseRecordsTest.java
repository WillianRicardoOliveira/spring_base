package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;

class ConviteOrganizacaoResponseRecordsTest {

    @Test
    @DisplayName("Deve criar detalhe de convite a partir do model")
    void deveCriarDetalheDeConviteAPartirDoModel() {
        LocalDateTime agora =
                LocalDateTime.of(2026, 8, 28, 10, 0);

        ConviteOrganizacaoModel convite =
                new ConviteOrganizacaoModel(
                        "Organizacao Principal",
                        "admin@teste.com",
                        "hash-token",
                        agora.plusDays(1)
                );

        DetalheConviteOrganizacaoRecord detalhe =
                new DetalheConviteOrganizacaoRecord(
                        convite,
                        agora
                );

        assertThat(detalhe.nomeOrganizacao())
                .isEqualTo("Organizacao Principal");

        assertThat(detalhe.emailAdministrador())
                .isEqualTo("admin@teste.com");

        assertThat(detalhe.expiraEm())
                .isEqualTo(agora.plusDays(1));

        assertThat(detalhe.aceitoEm())
                .isNull();

        assertThat(detalhe.status())
                .isEqualTo(StatusConviteOrganizacaoEnum.PENDENTE);

        assertThat(detalhe.expirado())
                .isFalse();

        assertThat(detalhe.auditoria())
                .isNotNull();
    }
}