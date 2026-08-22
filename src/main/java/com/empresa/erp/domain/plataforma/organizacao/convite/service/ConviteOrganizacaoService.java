package com.empresa.erp.domain.plataforma.organizacao.convite.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.TokenOpacoService;
import com.empresa.erp.domain.plataforma.organizacao.convite.config.ConviteOrganizacaoProperties;
import com.empresa.erp.domain.plataforma.organizacao.convite.event.ConviteOrganizacaoCriadoEvent;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.DetalheConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.repository.ConviteOrganizacaoRepository;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConviteOrganizacaoService {

    private final ConviteOrganizacaoRepository repository;

    private final UsuarioRepository usuarioRepository;

    private final TokenOpacoService tokenOpacoService;

    private final ConviteOrganizacaoProperties properties;

    private final ApplicationEventPublisher eventPublisher;

    private final Clock clock;

    @Transactional
    public DetalheConviteOrganizacaoRecord convidar(
            ConviteOrganizacaoRecord dados
    ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        String emailAdministrador =
                normalizarEmail(
                        dados.emailAdministrador()
                );

        validarAusenciaConvitePendente(
                emailAdministrador,
                agora
        );

        String token = tokenOpacoService.gerar();

        ConviteOrganizacaoModel convite =
                new ConviteOrganizacaoModel(
                        dados.nomeOrganizacao(),
                        emailAdministrador,
                        tokenOpacoService.gerarHash(token),
                        agora.plus(properties.validade())
                );

        repository.save(convite);

        eventPublisher.publishEvent(
                new ConviteOrganizacaoCriadoEvent(
                        convite.getId(),
                        convite.getEmailAdministrador(),
                        convite.getNomeOrganizacao(),
                        token,
                        convite.getExpiraEm()
                )
        );

        return new DetalheConviteOrganizacaoRecord(
                convite
        );
    }

    @Transactional(readOnly = true)
    public ConsultaConviteOrganizacaoRecord consultar(
            String token
    ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        ConviteOrganizacaoModel convite =
                buscarConviteValido(
                        token,
                        agora
                );

        boolean usuarioExistente =
                usuarioRepository.existsByEmailIgnoreCase(
                        convite.getEmailAdministrador()
                );

        return new ConsultaConviteOrganizacaoRecord(
                convite.getNomeOrganizacao(),
                mascararEmail(
                        convite.getEmailAdministrador()
                ),
                usuarioExistente
        );
    }

    private ConviteOrganizacaoModel buscarConviteValido(
            String token,
            LocalDateTime referencia
    ) {
        if (token == null || token.isBlank()) {
            throw conviteInvalido();
        }

        String tokenHash =
                tokenOpacoService.gerarHash(
                        token.trim()
                );

        ConviteOrganizacaoModel convite =
                repository
                        .buscarPorTokenHashEStatus(
                                tokenHash,
                                StatusConviteOrganizacaoEnum.PENDENTE
                        )
                        .orElseThrow(
                                this::conviteInvalido
                        );

        if (!convite.podeSerAceito(referencia)) {
            throw conviteInvalido();
        }

        return convite;
    }

    private void validarAusenciaConvitePendente(
            String emailAdministrador,
            LocalDateTime referencia
    ) {
        repository
                .buscarPendentePorEmailParaAtualizacao(
                        emailAdministrador
                )
                .ifPresent(convite -> {
                    if (convite.podeSerAceito(referencia)) {
                        throw new ValidacaoException(
                                "Ja existe um convite pendente "
                                        + "e valido para este e-mail."
                        );
                    }

                    convite.revogar();

                    repository.flush();
                });
    }

    private String mascararEmail(
            String email
    ) {
        int posicaoArroba = email.indexOf('@');

        if (posicaoArroba <= 0) {
            return "****";
        }

        return email.substring(0, 1)
                + "***"
                + email.substring(posicaoArroba);
    }

    private ValidacaoException conviteInvalido() {
        return new ValidacaoException(
                "Convite invalido ou expirado."
        );
    }

    private String normalizarEmail(
            String email
    ) {
        return email == null
                ? null
                : email
                        .trim()
                        .toLowerCase(Locale.ROOT);
    }
}