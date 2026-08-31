package com.empresa.erp.domain.plataforma.organizacao.convite.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.empresa.erp.domain.plataforma.organizacao.convite.config.ConviteOrganizacaoProperties;

@ExtendWith(MockitoExtension.class)
class EnvioConviteOrganizacaoSmtpAdapterTest {

    private static final LocalDateTime EXPIRA_EM =
            LocalDateTime.of(
                    2026,
                    8,
                    25,
                    10,
                    30
            );

    @Mock
    private JavaMailSender javaMailSender;

    private EnvioConviteOrganizacaoSmtpAdapter adapter;

    @BeforeEach
    void setUp() {
        var properties =
                new ConviteOrganizacaoProperties(
                        Duration.ofHours(48),
                        URI.create(
                                "https://erp.teste.com/"
                                        + "convites/aceite"
                        ),
                        "nao-responda@erp.teste.com"
                );

        adapter =
                new EnvioConviteOrganizacaoSmtpAdapter(
                        javaMailSender,
                        properties
                );
    }

    @Test
    @DisplayName(
            "Deve montar e enviar mensagem de convite"
    )
    void deveMontarEEnviarMensagemDeConvite() {
        adapter.enviar(
                "admin@teste.com",
                "Organização Principal",
                "token-convite",
                EXPIRA_EM
        );

        var mensagemCaptor =
                ArgumentCaptor.forClass(
                        SimpleMailMessage.class
                );

        verify(javaMailSender)
                .send(
                        mensagemCaptor.capture()
                );

        var mensagem =
                mensagemCaptor.getValue();

        assertThat(mensagem.getFrom())
                .isEqualTo(
                        "nao-responda@erp.teste.com"
                );

        assertThat(mensagem.getTo())
                .containsExactly(
                        "admin@teste.com"
                );

        assertThat(mensagem.getSubject())
                .isEqualTo(
                        "Convite para administrar uma organizacao"
                );

        assertThat(mensagem.getText())
                .contains(
                        "Voce foi convidado para administrar "
                                + "a organizacao "
                                + "Organização Principal.",
                        "https://erp.teste.com/convites/"
                                + "aceite?token=token-convite",
                        "Este convite expira em "
                                + "25/08/2026 as 10:30.",
                        "Se voce nao esperava este convite, "
                                + "ignore esta mensagem."
                );
    }

    @Test
    @DisplayName(
            "Deve codificar token ao montar URL de aceite"
    )
    void deveCodificarTokenAoMontarUrlDeAceite() {
        adapter.enviar(
                "admin@teste.com",
                "Organização Principal",
                "token com espaço&valor",
                EXPIRA_EM
        );

        var mensagemCaptor =
                ArgumentCaptor.forClass(
                        SimpleMailMessage.class
                );

        verify(javaMailSender)
                .send(
                        mensagemCaptor.capture()
                );

        assertThat(
                mensagemCaptor.getValue().getText()
        ).contains(
                "https://erp.teste.com/convites/aceite"
                        + "?token=token%20com%20"
                        + "espa%C3%A7o%26valor"
        );
    }

    @Test
    @DisplayName(
            "Deve propagar falha do servidor de e-mail"
    )
    void devePropagarFalhaDoServidorDeEmail() {
        doThrow(
                new MailSendException(
                        "Falha simulada no SMTP"
                )
        ).when(javaMailSender)
                .send(
                        any(SimpleMailMessage.class)
                );

        assertThatThrownBy(
                () -> adapter.enviar(
                        "admin@teste.com",
                        "Organização Principal",
                        "token-convite",
                        EXPIRA_EM
                )
        )
                .isInstanceOf(
                        MailSendException.class
                )
                .hasMessage(
                        "Falha simulada no SMTP"
                );
    }
}