package com.empresa.erp.domain.plataforma.organizacao.convite.adapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.empresa.erp.domain.plataforma.organizacao.convite.config.ConviteOrganizacaoProperties;
import com.empresa.erp.domain.plataforma.organizacao.convite.port.EnvioConviteOrganizacaoPort;

@Component
public class EnvioConviteOrganizacaoSmtpAdapter
        implements EnvioConviteOrganizacaoPort {

    private static final DateTimeFormatter
            FORMATADOR_DATA_HORA =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy 'as' HH:mm"
            );

    private final JavaMailSender javaMailSender;

    private final ConviteOrganizacaoProperties properties;

    public EnvioConviteOrganizacaoSmtpAdapter(
            JavaMailSender javaMailSender,
            ConviteOrganizacaoProperties properties
    ) {
        this.javaMailSender = javaMailSender;
        this.properties = properties;
    }

    @Override
    public void enviar(
            String emailDestino,
            String nomeOrganizacao,
            String token,
            LocalDateTime expiraEm
    ) {
        String urlAceite =
                criarUrlAceite(token);

        SimpleMailMessage mensagem =
                new SimpleMailMessage();

        mensagem.setFrom(properties.remetente());
        mensagem.setTo(emailDestino);
        mensagem.setSubject(
                "Convite para administrar uma organizacao"
        );
        mensagem.setText(
                criarConteudo(
                        nomeOrganizacao,
                        urlAceite,
                        expiraEm
                )
        );

        javaMailSender.send(mensagem);
    }

    private String criarUrlAceite(
            String token
    ) {
        return UriComponentsBuilder
                .fromUri(properties.urlAceite())
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }

    private String criarConteudo(
            String nomeOrganizacao,
            String urlAceite,
            LocalDateTime expiraEm
    ) {
        return """
                Voce foi convidado para administrar a organizacao %s.

                Para aceitar o convite, acesse:
                %s

                Este convite expira em %s.

                Se voce nao esperava este convite, ignore esta mensagem.
                """.formatted(
                nomeOrganizacao,
                urlAceite,
                expiraEm.format(
                        FORMATADOR_DATA_HORA
                )
        );
    }
}