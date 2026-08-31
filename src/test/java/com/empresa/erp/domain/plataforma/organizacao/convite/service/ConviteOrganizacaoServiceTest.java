package com.empresa.erp.domain.plataforma.organizacao.convite.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

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
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.repository.ConviteOrganizacaoRepository;
import com.empresa.erp.domain.usuario.criacao.service.CriacaoUsuarioService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConviteOrganizacaoServiceTest {

    private static final LocalDateTime AGORA =
            LocalDateTime.of(
                    2026,
                    8,
                    23,
                    10,
                    0
            );

    private static final Duration VALIDADE =
            Duration.ofHours(48);

    private static final String TOKEN =
            "token-original";

    private static final String TOKEN_HASH =
            "hash-token-original";

    @Mock
    private ConviteOrganizacaoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CriacaoUsuarioService
            criacaoUsuarioService;

    @Mock
    private ProvisionamentoOrganizacaoService
            provisionamentoOrganizacaoService;

    @Mock
    private UsuarioLogadoService usuarioLogadoService;

    @Mock
    private TokenOpacoService tokenOpacoService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ConviteOrganizacaoService service;

    @BeforeEach
    void setUp() {
        Clock clock =
                Clock.fixed(
                        Instant.parse(
                                "2026-08-23T13:00:00Z"
                        ),
                        ZoneId.of(
                                "America/Sao_Paulo"
                        )
                );

        var properties =
                new ConviteOrganizacaoProperties(
                        VALIDADE,
                        URI.create(
                                "https://erp.teste.com/convites/aceite"
                        ),
                        "nao-responda@erp.teste.com"
                );

        service =
                new ConviteOrganizacaoService(
                        repository,
                        usuarioRepository,
                        criacaoUsuarioService,
                        provisionamentoOrganizacaoService,
                        usuarioLogadoService,
                        tokenOpacoService,
                        properties,
                        eventPublisher,
                        clock
                );
    }

    @Test
    @DisplayName(
            "Deve criar convite normalizado e publicar evento"
    )
    void deveCriarConviteNormalizadoEPublicarEvento() {
        var dados =
                new ConviteOrganizacaoRecord(
                        "  Organização   Principal  ",
                        "  ADMIN@TESTE.COM  "
                );

        when(usuarioRepository.findByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(null);

        when(repository
                .buscarPendentePorEmailParaAtualizacao(
                        "admin@teste.com"
                )
        ).thenReturn(Optional.empty());

        when(tokenOpacoService.gerar())
                .thenReturn(TOKEN);

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository.save(
                any(ConviteOrganizacaoModel.class)
        )).thenAnswer(invocacao -> {
            ConviteOrganizacaoModel convite =
                    invocacao.getArgument(0);

            ReflectionTestUtils.setField(
                    convite,
                    "id",
                    10L
            );

            return convite;
        });

        var resultado =
                service.convidar(dados);

        assertThat(resultado.id())
                .isEqualTo(10L);

        assertThat(resultado.nomeOrganizacao())
                .isEqualTo(
                        "Organização Principal"
                );

        assertThat(resultado.emailAdministrador())
                .isEqualTo(
                        "admin@teste.com"
                );

        assertThat(resultado.expiraEm())
                .isEqualTo(
                        AGORA.plus(VALIDADE)
                );

        assertThat(resultado.status())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(resultado.expirado())
                .isFalse();

        var conviteCaptor =
                ArgumentCaptor.forClass(
                        ConviteOrganizacaoModel.class
                );

        verify(repository)
                .save(conviteCaptor.capture());

        var conviteSalvo =
                conviteCaptor.getValue();

        assertThat(conviteSalvo.getTokenHash())
                .isEqualTo(TOKEN_HASH);

        assertThat(conviteSalvo.getExpiraEm())
                .isEqualTo(
                        AGORA.plus(VALIDADE)
                );

        var eventoCaptor =
                ArgumentCaptor.forClass(
                        EnvioConviteOrganizacaoSolicitadoEvent.class
                );

        verify(eventPublisher)
                .publishEvent(
                        eventoCaptor.capture()
                );

        var evento =
                eventoCaptor.getValue();

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
                .isEqualTo(TOKEN);

        assertThat(evento.expiraEm())
                .isEqualTo(
                        AGORA.plus(VALIDADE)
                );
    }

    @Test
    @DisplayName(
            "Não deve criar convite para usuário inativo"
    )
    void naoDeveCriarConviteParaUsuarioInativo() {
        var usuario =
                criarUsuario(
                        20L,
                        "admin@teste.com",
                        StatusEnum.INATIVO
                );

        when(usuarioRepository.findByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(usuario);

        var dados =
                new ConviteOrganizacaoRecord(
                        "Organização",
                        "admin@teste.com"
                );

        assertThatThrownBy(
                () -> service.convidar(dados)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "O usuario deste e-mail esta inativo "
                                + "ou removido."
                );

        verifyNoInteractions(
                tokenOpacoService,
                eventPublisher
        );

        verify(repository, never())
                .save(any());
    }

    @Test
    @DisplayName(
            "Não deve criar convite quando já existe convite válido"
    )
    void naoDeveCriarConviteQuandoJaExisteConviteValido() {
        var conviteExistente =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(usuarioRepository.findByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(null);

        when(repository
                .buscarPendentePorEmailParaAtualizacao(
                        "admin@teste.com"
                )
        ).thenReturn(
                Optional.of(conviteExistente)
        );

        var dados =
                new ConviteOrganizacaoRecord(
                        "Organização",
                        "admin@teste.com"
                );

        assertThatThrownBy(
                () -> service.convidar(dados)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Ja existe um convite pendente "
                                + "e valido para este e-mail."
                );

        verify(repository, never()).flush();
        verify(repository, never()).save(any());

        verifyNoInteractions(
                tokenOpacoService,
                eventPublisher
        );
    }

    @Test
    @DisplayName(
            "Deve revogar convite expirado antes de criar outro"
    )
    void deveRevogarConviteExpiradoAntesDeCriarOutro() {
        var conviteExpirado =
                criarConvite(
                        10L,
                        AGORA.minusSeconds(1)
                );

        when(usuarioRepository.findByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(null);

        when(repository
                .buscarPendentePorEmailParaAtualizacao(
                        "admin@teste.com"
                )
        ).thenReturn(
                Optional.of(conviteExpirado)
        );

        when(tokenOpacoService.gerar())
                .thenReturn(TOKEN);

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository.save(
                any(ConviteOrganizacaoModel.class)
        )).thenAnswer(
                invocacao ->
                        invocacao.getArgument(0)
        );

        service.convidar(
                new ConviteOrganizacaoRecord(
                        "Organização",
                        "admin@teste.com"
                )
        );

        assertThat(conviteExpirado.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.REVOGADO
                );

        verify(repository).flush();
        verify(repository)
                .save(any(
                        ConviteOrganizacaoModel.class
                ));

        verify(eventPublisher)
                .publishEvent(any(
                        EnvioConviteOrganizacaoSolicitadoEvent.class
                ));
    }

    @Test
    @DisplayName(
            "Deve consultar convite válido e mascarar e-mail"
    )
    void deveConsultarConviteValidoEMascararEmail() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository.buscarPorTokenHashEStatus(
                TOKEN_HASH,
                StatusConviteOrganizacaoEnum.PENDENTE
        )).thenReturn(
                Optional.of(convite)
        );

        when(usuarioRepository.existsByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(true);

        var resultado =
                service.consultar(TOKEN);

        assertThat(resultado.nomeOrganizacao())
                .isEqualTo("Organização");

        assertThat(
                resultado.emailAdministradorMascarado()
        ).isEqualTo(
                "a***@teste.com"
        );

        assertThat(resultado.usuarioExistente())
                .isTrue();
    }

    @Test
    @DisplayName(
            "Não deve consultar convite com token em branco"
    )
    void naoDeveConsultarConviteComTokenEmBranco() {
        assertThatThrownBy(
                () -> service.consultar("  ")
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Convite invalido ou expirado."
                );

        verifyNoInteractions(
                tokenOpacoService,
                repository,
                usuarioRepository
        );
    }

    @Test
    @DisplayName(
            "Não deve consultar convite inexistente"
    )
    void naoDeveConsultarConviteInexistente() {
        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository.buscarPorTokenHashEStatus(
                TOKEN_HASH,
                StatusConviteOrganizacaoEnum.PENDENTE
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.consultar(TOKEN)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Convite invalido ou expirado."
                );

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName(
            "Não deve consultar convite expirado"
    )
    void naoDeveConsultarConviteExpirado() {
        var convite =
                criarConvite(
                        10L,
                        AGORA
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository.buscarPorTokenHashEStatus(
                TOKEN_HASH,
                StatusConviteOrganizacaoEnum.PENDENTE
        )).thenReturn(
                Optional.of(convite)
        );

        assertThatThrownBy(
                () -> service.consultar(TOKEN)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Convite invalido ou expirado."
                );

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName(
            "Deve listar convites com filtro normalizado"
    )
    void deveListarConvitesComFiltroNormalizado() {
        var paginacao =
                PageRequest.of(0, 10);

        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(repository.listar(
                paginacao,
                "Organização",
                StatusConviteOrganizacaoEnum.PENDENTE
        )).thenReturn(
                new PageImpl<>(
                        List.of(convite),
                        paginacao,
                        1
                )
        );

        var resultado =
                service.listar(
                        paginacao,
                        "  Organização  ",
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(resultado.getContent())
                .hasSize(1);

        assertThat(
                resultado.getContent().get(0).id()
        ).isEqualTo(10L);

        assertThat(
                resultado.getContent().get(0).expirado()
        ).isFalse();
    }

    @Test
    @DisplayName(
            "Deve converter filtro em branco para nulo"
    )
    void deveConverterFiltroEmBrancoParaNulo() {
        var paginacao =
                PageRequest.of(0, 10);

        when(repository.listar(
                paginacao,
                null,
                null
        )).thenReturn(
                new PageImpl<>(
                        List.of(),
                        paginacao,
                        0
                )
        );

        var resultado =
                service.listar(
                        paginacao,
                        "   ",
                        null
                );

        assertThat(resultado).isEmpty();

        verify(repository).listar(
                paginacao,
                null,
                null
        );
    }

    @Test
    @DisplayName(
            "Deve detalhar convite"
    )
    void deveDetalharConvite() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(repository.findById(10L))
                .thenReturn(
                        Optional.of(convite)
                );

        var resultado =
                service.detalhar(10L);

        assertThat(resultado.id())
                .isEqualTo(10L);

        assertThat(resultado.nomeOrganizacao())
                .isEqualTo("Organização");

        assertThat(resultado.status())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(resultado.expirado())
                .isFalse();
    }

    @Test
    @DisplayName(
            "Deve lançar erro ao detalhar convite inexistente"
    )
    void deveLancarErroAoDetalharConviteInexistente() {
        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.detalhar(99L)
        ).isInstanceOf(
                EntityNotFoundException.class
        );
    }

    @Test
    @DisplayName(
            "Deve revogar convite pendente"
    )
    void deveRevogarConvitePendente() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(repository
                .buscarPorIdParaAtualizacao(10L)
        ).thenReturn(
                Optional.of(convite)
        );

        service.revogar(10L);

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.REVOGADO
                );
    }

    @Test
    @DisplayName(
            "Deve manter revogação idempotente"
    )
    void deveManterRevogacaoIdempotente() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        convite.revogar();

        when(repository
                .buscarPorIdParaAtualizacao(10L)
        ).thenReturn(
                Optional.of(convite)
        );

        service.revogar(10L);

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.REVOGADO
                );
    }

    @Test
    @DisplayName(
            "Não deve revogar convite aceito"
    )
    void naoDeveRevogarConviteAceito() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        convite.aceitar(
                AGORA.minusMinutes(10)
        );

        when(repository
                .buscarPorIdParaAtualizacao(10L)
        ).thenReturn(
                Optional.of(convite)
        );

        assertThatThrownBy(
                () -> service.revogar(10L)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Convite aceito nao pode ser revogado."
                );
    }

    @Test
    @DisplayName(
            "Deve lançar erro ao revogar convite inexistente"
    )
    void deveLancarErroAoRevogarConviteInexistente() {
        when(repository
                .buscarPorIdParaAtualizacao(99L)
        ).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.revogar(99L)
        ).isInstanceOf(
                EntityNotFoundException.class
        );
    }

    @Test
    @DisplayName(
            "Deve renovar convite pendente e publicar evento"
    )
    void deveRenovarConvitePendenteEPublicarEvento() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.minusHours(1)
                );

        when(repository
                .buscarPorIdParaAtualizacao(10L)
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioRepository.findByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(null);

        when(tokenOpacoService.gerar())
                .thenReturn("novo-token");

        when(tokenOpacoService.gerarHash(
                "novo-token"
        )).thenReturn("novo-hash");

        var resultado =
                service.reenviar(10L);

        assertThat(convite.getTokenHash())
                .isEqualTo("novo-hash");

        assertThat(convite.getExpiraEm())
                .isEqualTo(
                        AGORA.plus(VALIDADE)
                );

        assertThat(resultado.expirado())
                .isFalse();

        var eventoCaptor =
                ArgumentCaptor.forClass(
                        EnvioConviteOrganizacaoSolicitadoEvent.class
                );

        verify(eventPublisher)
                .publishEvent(
                        eventoCaptor.capture()
                );

        assertThat(
                eventoCaptor.getValue().token()
        ).isEqualTo("novo-token");

        assertThat(
                eventoCaptor.getValue().expiraEm()
        ).isEqualTo(
                AGORA.plus(VALIDADE)
        );
    }

    @Test
    @DisplayName(
            "Não deve reenviar convite aceito"
    )
    void naoDeveReenviarConviteAceito() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        convite.aceitar(
                AGORA.minusMinutes(10)
        );

        when(repository
                .buscarPorIdParaAtualizacao(10L)
        ).thenReturn(
                Optional.of(convite)
        );

        assertThatThrownBy(
                () -> service.reenviar(10L)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Somente convites pendentes "
                                + "podem ser reenviados."
                );

        verifyNoInteractions(
                tokenOpacoService,
                eventPublisher
        );
    }

    @Test
    @DisplayName(
            "Não deve reenviar convite para usuário inativo"
    )
    void naoDeveReenviarConviteParaUsuarioInativo() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.minusHours(1)
                );

        var usuario =
                criarUsuario(
                        20L,
                        "admin@teste.com",
                        StatusEnum.INATIVO
                );

        when(repository
                .buscarPorIdParaAtualizacao(10L)
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioRepository.findByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(usuario);

        assertThatThrownBy(
                () -> service.reenviar(10L)
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "O usuario deste e-mail esta inativo "
                                + "ou removido."
                );

        verifyNoInteractions(
                tokenOpacoService,
                eventPublisher
        );
    }

    @Test
    @DisplayName(
            "Deve aceitar convite com usuário existente"
    )
    void deveAceitarConviteComUsuarioExistente() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        var usuario =
                criarUsuario(
                        20L,
                        "admin@teste.com",
                        StatusEnum.ATIVO
                );

        var organizacao =
                criarOrganizacao(
                        30L,
                        "Organização"
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository
                .buscarPorTokenHashEStatusParaAtualizacao(
                        TOKEN_HASH,
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioLogadoService.getId())
                .thenReturn(20L);

        when(usuarioRepository.findByIdAndStatus(
                20L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuario)
        );

        when(provisionamentoOrganizacaoService
                .provisionar(
                        "Organização",
                        usuario
                )
        ).thenReturn(organizacao);

        var resultado =
                service.aceitarUsuarioExistente(
                        new AceiteConviteOrganizacaoUsuarioExistenteRecord(
                                TOKEN
                        )
                );

        assertThat(resultado.idOrganizacao())
                .isEqualTo(30L);

        assertThat(resultado.nomeOrganizacao())
                .isEqualTo("Organização");

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.ACEITO
                );

        assertThat(convite.getAceitoEm())
                .isEqualTo(AGORA);
    }

    @Test
    @DisplayName(
            "Não deve aceitar convite com usuário autenticado diferente"
    )
    void naoDeveAceitarConviteComUsuarioAutenticadoDiferente() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        var usuario =
                criarUsuario(
                        20L,
                        "outro@teste.com",
                        StatusEnum.ATIVO
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository
                .buscarPorTokenHashEStatusParaAtualizacao(
                        TOKEN_HASH,
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioLogadoService.getId())
                .thenReturn(20L);

        when(usuarioRepository.findByIdAndStatus(
                20L,
                StatusEnum.ATIVO
        )).thenReturn(
                Optional.of(usuario)
        );

        assertThatThrownBy(
                () -> service.aceitarUsuarioExistente(
                        new AceiteConviteOrganizacaoUsuarioExistenteRecord(
                                TOKEN
                        )
                )
        )
                .isInstanceOf(
                        AccessDeniedException.class
                )
                .hasMessage(
                        "Acesso negado."
                );

        verifyNoInteractions(
                provisionamentoOrganizacaoService
        );

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar convite sem usuário autenticado ativo"
    )
    void naoDeveAceitarConviteSemUsuarioAutenticadoAtivo() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository
                .buscarPorTokenHashEStatusParaAtualizacao(
                        TOKEN_HASH,
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioLogadoService.getId())
                .thenReturn(20L);

        when(usuarioRepository.findByIdAndStatus(
                20L,
                StatusEnum.ATIVO
        )).thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.aceitarUsuarioExistente(
                        new AceiteConviteOrganizacaoUsuarioExistenteRecord(
                                TOKEN
                        )
                )
        )
                .isInstanceOf(
                        AccessDeniedException.class
                )
                .hasMessage(
                        "Acesso negado."
                );

        verifyNoInteractions(
                provisionamentoOrganizacaoService
        );
    }

    @Test
    @DisplayName(
            "Deve aceitar convite criando novo usuário"
    )
    void deveAceitarConviteCriandoNovoUsuario() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        var usuario =
                criarUsuario(
                        20L,
                        "admin@teste.com",
                        StatusEnum.ATIVO
                );

        var organizacao =
                criarOrganizacao(
                        30L,
                        "Organização"
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository
                .buscarPorTokenHashEStatusParaAtualizacao(
                        TOKEN_HASH,
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioRepository.existsByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(false);

        when(criacaoUsuarioService.criar(
                "admin@teste.com",
                "Senha@123"
        )).thenReturn(usuario);

        when(provisionamentoOrganizacaoService
                .provisionar(
                        "Organização",
                        usuario
                )
        ).thenReturn(organizacao);

        var resultado =
                service.aceitarNovoUsuario(
                        new AceiteConviteOrganizacaoNovoUsuarioRecord(
                                TOKEN,
                                "Senha@123"
                        )
                );

        assertThat(resultado.idOrganizacao())
                .isEqualTo(30L);

        assertThat(resultado.nomeOrganizacao())
                .isEqualTo("Organização");

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.ACEITO
                );

        assertThat(convite.getAceitoEm())
                .isEqualTo(AGORA);
    }

    @Test
    @DisplayName(
            "Não deve criar usuário quando já existe conta para o convite"
    )
    void naoDeveCriarUsuarioQuandoJaExisteContaParaOConvite() {
        var convite =
                criarConvite(
                        10L,
                        AGORA.plusHours(1)
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository
                .buscarPorTokenHashEStatusParaAtualizacao(
                        TOKEN_HASH,
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        ).thenReturn(
                Optional.of(convite)
        );

        when(usuarioRepository.existsByEmailIgnoreCase(
                "admin@teste.com"
        )).thenReturn(true);

        assertThatThrownBy(
                () -> service.aceitarNovoUsuario(
                        new AceiteConviteOrganizacaoNovoUsuarioRecord(
                                TOKEN,
                                "Senha@123"
                        )
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Ja existe uma conta para este convite. "
                                + "Entre no sistema para aceita-lo."
                );

        verifyNoInteractions(
                criacaoUsuarioService,
                provisionamentoOrganizacaoService
        );

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );
    }

    @Test
    @DisplayName(
            "Não deve aceitar novo usuário com convite expirado"
    )
    void naoDeveAceitarNovoUsuarioComConviteExpirado() {
        var convite =
                criarConvite(
                        10L,
                        AGORA
                );

        when(tokenOpacoService.gerarHash(TOKEN))
                .thenReturn(TOKEN_HASH);

        when(repository
                .buscarPorTokenHashEStatusParaAtualizacao(
                        TOKEN_HASH,
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        ).thenReturn(
                Optional.of(convite)
        );

        assertThatThrownBy(
                () -> service.aceitarNovoUsuario(
                        new AceiteConviteOrganizacaoNovoUsuarioRecord(
                                TOKEN,
                                "Senha@123"
                        )
                )
        )
                .isInstanceOf(
                        ValidacaoException.class
                )
                .hasMessage(
                        "Convite invalido ou expirado."
                );

        verifyNoInteractions(
                criacaoUsuarioService,
                provisionamentoOrganizacaoService
        );
    }

    private ConviteOrganizacaoModel criarConvite(
            Long id,
            LocalDateTime expiraEm
    ) {
        var convite =
                new ConviteOrganizacaoModel(
                        "Organização",
                        "admin@teste.com",
                        TOKEN_HASH,
                        expiraEm
                );

        ReflectionTestUtils.setField(
                convite,
                "id",
                id
        );

        return convite;
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email,
            StatusEnum status
    ) {
        return new UsuarioModel(
                id,
                email,
                "senha-criptografada",
                status
        );
    }

    private OrganizacaoModel criarOrganizacao(
            Long id,
            String nome
    ) {
        var organizacao =
                new OrganizacaoModel(nome);

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                id
        );

        return organizacao;
    }
}