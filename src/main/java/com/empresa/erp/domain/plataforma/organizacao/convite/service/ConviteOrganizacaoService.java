package com.empresa.erp.domain.plataforma.organizacao.convite.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.service.TokenOpacoService;
import com.empresa.erp.core.security.service.UsuarioLogadoService;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.organizacao.provisionamento.service.ProvisionamentoOrganizacaoService;
import com.empresa.erp.domain.plataforma.organizacao.convite.config.ConviteOrganizacaoProperties;
import com.empresa.erp.domain.plataforma.organizacao.convite.event.EnvioConviteOrganizacaoSolicitadoEvent;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.ConviteOrganizacaoModel;
import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoNovoUsuarioRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoUsuarioExistenteRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.DetalheConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ListaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ResultadoAceiteConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.repository.ConviteOrganizacaoRepository;
import com.empresa.erp.domain.usuario.criacao.service.CriacaoUsuarioService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConviteOrganizacaoService {

    private final ConviteOrganizacaoRepository repository;

    private final UsuarioRepository usuarioRepository;

    private final CriacaoUsuarioService
            criacaoUsuarioService;

    private final ProvisionamentoOrganizacaoService
            provisionamentoOrganizacaoService;

    private final UsuarioLogadoService usuarioLogadoService;

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

        validarUsuarioExistenteDisponivel(
                emailAdministrador
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
                new EnvioConviteOrganizacaoSolicitadoEvent(
                        convite.getId(),
                        convite.getEmailAdministrador(),
                        convite.getNomeOrganizacao(),
                        token,
                        convite.getExpiraEm()
                )
        );

        return new DetalheConviteOrganizacaoRecord(
                convite,
                agora
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

    @Transactional(readOnly = true)
    public Page<ListaConviteOrganizacaoRecord> listar(
            Pageable paginacao,
            String filtro,
            StatusConviteOrganizacaoEnum status
    ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        String filtroNormalizado =
                normalizarFiltro(filtro);

        return repository
                .listar(
                        paginacao,
                        filtroNormalizado,
                        status
                )
                .map(convite ->
                        new ListaConviteOrganizacaoRecord(
                                convite,
                                agora
                        )
                );
    }

    @Transactional(readOnly = true)
    public DetalheConviteOrganizacaoRecord detalhar(
            Long id
    ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        ConviteOrganizacaoModel convite =
                repository.findById(id)
                        .orElseThrow(
                                EntityNotFoundException::new
                        );

        return new DetalheConviteOrganizacaoRecord(
                convite,
                agora
        );
    }

    @Transactional
    public void revogar(
            Long id
    ) {
        ConviteOrganizacaoModel convite =
                repository
                        .buscarPorIdParaAtualizacao(id)
                        .orElseThrow(
                                EntityNotFoundException::new
                        );

        if (StatusConviteOrganizacaoEnum.REVOGADO
                .equals(convite.getStatus())) {
            return;
        }

        if (!StatusConviteOrganizacaoEnum.PENDENTE
                .equals(convite.getStatus())) {
            throw new ValidacaoException(
                    "Convite aceito nao pode ser revogado."
            );
        }

        convite.revogar();
    }
    
    @Transactional
    public DetalheConviteOrganizacaoRecord reenviar(
            Long id
    ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        ConviteOrganizacaoModel convite =
                repository
                        .buscarPorIdParaAtualizacao(id)
                        .orElseThrow(
                                EntityNotFoundException::new
                        );

        if (!StatusConviteOrganizacaoEnum.PENDENTE
                .equals(convite.getStatus())) {
            throw new ValidacaoException(
                    "Somente convites pendentes "
                            + "podem ser reenviados."
            );
        }

        validarUsuarioExistenteDisponivel(
                convite.getEmailAdministrador()
        );

        String token = tokenOpacoService.gerar();

        convite.renovar(
                tokenOpacoService.gerarHash(token),
                agora.plus(properties.validade())
        );

        eventPublisher.publishEvent(
                new EnvioConviteOrganizacaoSolicitadoEvent(
                        convite.getId(),
                        convite.getEmailAdministrador(),
                        convite.getNomeOrganizacao(),
                        token,
                        convite.getExpiraEm()
                )
        );

        return new DetalheConviteOrganizacaoRecord(
                convite,
                agora
        );
    }

    @Transactional
    public ResultadoAceiteConviteOrganizacaoRecord
            aceitarUsuarioExistente(
                    AceiteConviteOrganizacaoUsuarioExistenteRecord
                            dados
            ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        ConviteOrganizacaoModel convite =
                buscarConviteValidoParaAtualizacao(
                        dados.token(),
                        agora
                );

        UsuarioModel administrador =
                buscarUsuarioAutenticadoAtivo();

        validarUsuarioDoConvite(
                convite,
                administrador
        );

        OrganizacaoModel organizacao =
                provisionamentoOrganizacaoService
                        .provisionar(
                                convite.getNomeOrganizacao(),
                                administrador
                        );

        convite.aceitar(agora);

        return new ResultadoAceiteConviteOrganizacaoRecord(
                organizacao
        );
    }

    @Transactional
    public ResultadoAceiteConviteOrganizacaoRecord
            aceitarNovoUsuario(
                    AceiteConviteOrganizacaoNovoUsuarioRecord
                            dados
            ) {
        LocalDateTime agora =
                LocalDateTime.now(clock);

        ConviteOrganizacaoModel convite =
                buscarConviteValidoParaAtualizacao(
                        dados.token(),
                        agora
                );

        validarAusenciaUsuarioParaConvite(
                convite
        );

        UsuarioModel administrador =
                criacaoUsuarioService.criar(
                        convite.getEmailAdministrador(),
                        dados.senha()
                );

        OrganizacaoModel organizacao =
                provisionamentoOrganizacaoService
                        .provisionar(
                                convite.getNomeOrganizacao(),
                                administrador
                        );

        convite.aceitar(agora);

        return new ResultadoAceiteConviteOrganizacaoRecord(
                organizacao
        );
    }

    private ConviteOrganizacaoModel buscarConviteValido(
            String token,
            LocalDateTime referencia
    ) {
        String tokenHash =
                gerarHashToken(token);

        ConviteOrganizacaoModel convite =
                repository
                        .buscarPorTokenHashEStatus(
                                tokenHash,
                                StatusConviteOrganizacaoEnum.PENDENTE
                        )
                        .orElseThrow(
                                this::conviteInvalido
                        );

        validarConviteNaoExpirado(
                convite,
                referencia
        );

        return convite;
    }

    private ConviteOrganizacaoModel
            buscarConviteValidoParaAtualizacao(
                    String token,
                    LocalDateTime referencia
            ) {
        String tokenHash =
                gerarHashToken(token);

        ConviteOrganizacaoModel convite =
                repository
                        .buscarPorTokenHashEStatusParaAtualizacao(
                                tokenHash,
                                StatusConviteOrganizacaoEnum.PENDENTE
                        )
                        .orElseThrow(
                                this::conviteInvalido
                        );

        validarConviteNaoExpirado(
                convite,
                referencia
        );

        return convite;
    }

    private String gerarHashToken(
            String token
    ) {
        if (token == null || token.isBlank()) {
            throw conviteInvalido();
        }

        return tokenOpacoService.gerarHash(
                token.trim()
        );
    }

    private void validarConviteNaoExpirado(
            ConviteOrganizacaoModel convite,
            LocalDateTime referencia
    ) {
        if (!convite.podeSerAceito(referencia)) {
            throw conviteInvalido();
        }
    }

    private UsuarioModel buscarUsuarioAutenticadoAtivo() {
        Long idUsuario =
                usuarioLogadoService.getId();

        return usuarioRepository
                .findByIdAndStatus(
                        idUsuario,
                        StatusEnum.ATIVO
                )
                .orElseThrow(
                        () -> new AccessDeniedException(
                                "Acesso negado."
                        )
                );
    }

    private void validarUsuarioDoConvite(
            ConviteOrganizacaoModel convite,
            UsuarioModel usuario
    ) {
        if (!convite.getEmailAdministrador()
                .equalsIgnoreCase(usuario.getEmail())) {
            throw new AccessDeniedException(
                    "Acesso negado."
            );
        }
    }

    private void validarAusenciaUsuarioParaConvite(
            ConviteOrganizacaoModel convite
    ) {
        boolean usuarioExistente =
                usuarioRepository.existsByEmailIgnoreCase(
                        convite.getEmailAdministrador()
                );

        if (usuarioExistente) {
            throw new ValidacaoException(
                    "Ja existe uma conta para este convite. "
                            + "Entre no sistema para aceita-lo."
            );
        }
    }

    private void validarUsuarioExistenteDisponivel(
            String emailAdministrador
    ) {
        UsuarioModel usuario =
                usuarioRepository.findByEmailIgnoreCase(
                        emailAdministrador
                );

        if (usuario != null && !usuario.isEnabled()) {
            throw new ValidacaoException(
                    "O usuario deste e-mail esta inativo "
                            + "ou removido."
            );
        }
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

    private String normalizarFiltro(
            String filtro
    ) {
        return filtro == null || filtro.isBlank()
                ? null
                : filtro.trim();
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