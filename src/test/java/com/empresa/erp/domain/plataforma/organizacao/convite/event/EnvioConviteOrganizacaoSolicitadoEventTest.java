package com.empresa.erp.domain.plataforma.organizacao.convite.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnvioConviteOrganizacaoSolicitadoEventTest {

    private static final LocalDateTime EXPIRA_EM =
            LocalDateTime.of(
                    2026,
                    8,
                    25,
                    10,
                    0
            );

    @Test
    @DisplayName(
            "Deve preservar os dados necessários para o envio"
    )
    void devePreservarOsDadosNecessariosParaOEnvio() {
        var evento =
                criarEvento();

        assertThat(evento.idConvite())
                .isEqualTo(10L);

        assertThat(evento.emailDestino())
                .isEqualTo(
                        "admin@teste.com"
                );

        assertThat(evento.nomeOrganizacao())
                .isEqualTo(
                        "Organização Principal"
                );

        assertThat(evento.token())
                .isEqualTo(
                        "token-secreto"
                );

        assertThat(evento.expiraEm())
                .isEqualTo(EXPIRA_EM);
    }

    @Test
    @DisplayName(
            "Não deve expor dados sensíveis no toString"
    )
    void naoDeveExporDadosSensiveisNoToString() {
        var evento =
                criarEvento();

        String texto =
                evento.toString();

        assertThat(texto)
                .contains(
                        "idConvite=10",
                        "emailDestino=****",
                        "nomeOrganizacao=****",
                        "token=****",
                        "expiraEm=" + EXPIRA_EM
                );

        assertThat(texto)
                .doesNotContain(
                        "admin@teste.com",
                        "Organização Principal",
                        "token-secreto"
                );
    }

    private EnvioConviteOrganizacaoSolicitadoEvent
            criarEvento() {
        return new EnvioConviteOrganizacaoSolicitadoEvent(
                10L,
                "admin@teste.com",
                "Organização Principal",
                "token-secreto",
                EXPIRA_EM
        );
    }
}