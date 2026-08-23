package com.empresa.erp.domain.plataforma.organizacao.convite.event;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.empresa.erp.domain.plataforma.organizacao.convite.port.EnvioConviteOrganizacaoPort;

@ExtendWith(MockitoExtension.class)
class EnvioConviteOrganizacaoSolicitadoListenerTest {

    private static final LocalDateTime EXPIRA_EM =
            LocalDateTime.of(
                    2026,
                    8,
                    25,
                    10,
                    0
            );

    @Mock
    private EnvioConviteOrganizacaoPort
            envioConviteOrganizacaoPort;

    @InjectMocks
    private EnvioConviteOrganizacaoSolicitadoListener
            listener;

    @Test
    @DisplayName(
            "Deve encaminhar evento para o serviço de envio"
    )
    void deveEncaminharEventoParaOServicoDeEnvio() {
        var evento =
                criarEvento();

        listener.processar(evento);

        verify(envioConviteOrganizacaoPort)
                .enviar(
                        "admin@teste.com",
                        "Organização Principal",
                        "token-secreto",
                        EXPIRA_EM
                );
    }

    @Test
    @DisplayName(
            "Não deve propagar falha ocorrida durante o envio"
    )
    void naoDevePropagarFalhaOcorridaDuranteOEnvio() {
        var evento =
                criarEvento();

        doThrow(
                new RuntimeException(
                        "Falha simulada no SMTP"
                )
        ).when(envioConviteOrganizacaoPort)
                .enviar(
                        "admin@teste.com",
                        "Organização Principal",
                        "token-secreto",
                        EXPIRA_EM
                );

        assertThatCode(
                () -> listener.processar(evento)
        ).doesNotThrowAnyException();

        verify(envioConviteOrganizacaoPort)
                .enviar(
                        "admin@teste.com",
                        "Organização Principal",
                        "token-secreto",
                        EXPIRA_EM
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