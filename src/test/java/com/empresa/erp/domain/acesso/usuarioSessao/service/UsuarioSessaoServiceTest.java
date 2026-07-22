package com.empresa.erp.domain.acesso.usuarioSessao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.exception.RefreshTokenException;
import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.record.TokenGeradoSecurity;
import com.empresa.erp.domain.acesso.usuarioSessao.model.UsuarioSessaoModel;
import com.empresa.erp.domain.acesso.usuarioSessao.repository.UsuarioSessaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioSessaoServiceTest {

    private UsuarioSessaoRepository repository;
    private TokenSecurity tokenSecurity;
    private UsuarioSessaoService service;

    @BeforeEach
    void setUp() {
        repository = org.mockito.Mockito.mock(UsuarioSessaoRepository.class);
        tokenSecurity = org.mockito.Mockito.mock(TokenSecurity.class);
        service = new UsuarioSessaoService(repository, tokenSecurity);

        ReflectionTestUtils.setField(service, "refreshTokenExpirationDays", 7L);
    }

    @Test
    @DisplayName("Deve criar sessao salvando apenas hash do refresh token")
    void deveCriarSessaoSalvandoApenasHashDoRefreshToken() {
        var usuario = criarUsuario(1L, "admin@futuro.com");

        String refreshToken = service.criarSessao(usuario, "jti-access", "127.0.0.1", "Postman");

        ArgumentCaptor<UsuarioSessaoModel> captor = ArgumentCaptor.forClass(UsuarioSessaoModel.class);
        verify(repository).save(captor.capture());

        UsuarioSessaoModel sessao = captor.getValue();

        assertThat(refreshToken).isNotBlank();
        assertThat(sessao.getUsuario()).isEqualTo(usuario);
        assertThat(sessao.getRefreshTokenHash()).isNotBlank();
        assertThat(sessao.getRefreshTokenHash()).isNotEqualTo(refreshToken);
        assertThat(sessao.getAccessTokenJti()).isEqualTo("jti-access");
        assertThat(sessao.getIp()).isEqualTo("127.0.0.1");
        assertThat(sessao.getUserAgent()).isEqualTo("Postman");
        assertThat(sessao.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(sessao.estaAtiva()).isTrue();
    }

    @Test
    @DisplayName("Deve renovar sessao com sucesso")
    void deveRenovarSessaoComSucesso() {
        var usuario = criarUsuario(1L, "admin@futuro.com");
        String refreshTokenAtual = "refresh-token-atual";
        String refreshTokenHashAtual = service.gerarHash(refreshTokenAtual);

        var sessao = criarSessao(usuario, refreshTokenHashAtual, "jti-antigo");

        when(repository.findByRefreshTokenHashAndStatus(refreshTokenHashAtual, StatusEnum.ATIVO))
                .thenReturn(Optional.of(sessao));
        when(tokenSecurity.gerarTokenComJti(usuario))
                .thenReturn(new TokenGeradoSecurity("novo-jwt-token", "novo-jti"));

        var resultado = service.renovarSessao(refreshTokenAtual);

        assertThat(resultado.token()).isEqualTo("novo-jwt-token");
        assertThat(resultado.refreshToken()).isNotBlank();
        assertThat(resultado.refreshToken()).isNotEqualTo(refreshTokenAtual);
        assertThat(sessao.getRefreshTokenHash()).isEqualTo(service.gerarHash(resultado.refreshToken()));
        assertThat(sessao.getAccessTokenJti()).isEqualTo("novo-jti");
        assertThat(sessao.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(sessao.estaAtiva()).isTrue();

        verify(repository).findByRefreshTokenHashAndStatus(refreshTokenHashAtual, StatusEnum.ATIVO);
        verify(tokenSecurity).gerarTokenComJti(usuario);
    }

    @Test
    @DisplayName("Deve revogar sessao com sucesso")
    void deveRevogarSessaoComSucesso() {
        var usuario = criarUsuario(1L, "admin@futuro.com");
        String refreshToken = "refresh-token-logout";
        String refreshTokenHash = service.gerarHash(refreshToken);
        var sessao = criarSessao(usuario, refreshTokenHash, "jti-atual");

        when(repository.findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO))
                .thenReturn(Optional.of(sessao));

        service.revogarSessao(refreshToken);

        assertThat(sessao.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(sessao.getRevogadoEm()).isNotNull();
        assertThat(sessao.getRevogadoPor()).isEqualTo(1L);
        assertThat(sessao.getMotivoRevogacao()).isEqualTo("LOGOUT");
        assertThat(sessao.estaAtiva()).isFalse();

        verify(repository).findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve revogar todas as sessoes ativas do usuario")
    void deveRevogarTodasAsSessoesAtivasDoUsuario() {
        var usuario = criarUsuario(1L, "admin@futuro.com");
        var primeiraSessao = criarSessao(usuario, "hash-1", "jti-1");
        var segundaSessao = criarSessao(usuario, "hash-2", "jti-2");

        when(repository.findAllByUsuarioIdAndStatus(1L, StatusEnum.ATIVO))
                .thenReturn(List.of(primeiraSessao, segundaSessao));

        service.revogarSessoesDoUsuario(1L, 99L, "ALTERACAO_SENHA");

        assertThat(primeiraSessao.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(primeiraSessao.getRevogadoPor()).isEqualTo(99L);
        assertThat(primeiraSessao.getMotivoRevogacao()).isEqualTo("ALTERACAO_SENHA");

        assertThat(segundaSessao.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(segundaSessao.getRevogadoPor()).isEqualTo(99L);
        assertThat(segundaSessao.getMotivoRevogacao()).isEqualTo("ALTERACAO_SENHA");

        verify(repository).findAllByUsuarioIdAndStatus(1L, StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve revogar sessoes expiradas")
    void deveRevogarSessoesExpiradas() {
        var usuario = criarUsuario(1L, "admin@futuro.com");

        var primeiraSessao = new UsuarioSessaoModel(
                usuario,
                "hash-expirado-1",
                "jti-1",
                LocalDateTime.now().minusDays(1),
                "127.0.0.1",
                "Postman"
        );

        var segundaSessao = new UsuarioSessaoModel(
                usuario,
                "hash-expirado-2",
                "jti-2",
                LocalDateTime.now().minusHours(1),
                "127.0.0.1",
                "Postman"
        );

        when(repository.findAllByExpiraEmBeforeAndStatus(
                org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.eq(StatusEnum.ATIVO)
        )).thenReturn(List.of(primeiraSessao, segundaSessao));

        service.revogarSessoesExpiradas();

        assertThat(primeiraSessao.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(primeiraSessao.getRevogadoPor()).isEqualTo(0L);
        assertThat(primeiraSessao.getMotivoRevogacao()).isEqualTo("REFRESH_TOKEN_EXPIRADO");

        assertThat(segundaSessao.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(segundaSessao.getRevogadoPor()).isEqualTo(0L);
        assertThat(segundaSessao.getMotivoRevogacao()).isEqualTo("REFRESH_TOKEN_EXPIRADO");

        verify(repository).findAllByExpiraEmBeforeAndStatus(
                org.mockito.ArgumentMatchers.any(LocalDateTime.class),
                org.mockito.ArgumentMatchers.eq(StatusEnum.ATIVO)
        );
    }

    @Test
    @DisplayName("Deve bloquear revogacao de sessoes sem usuario")
    void deveBloquearRevogacaoDeSessoesSemUsuario() {
        assertThatThrownBy(() -> service.revogarSessoesDoUsuario(null, 99L, "ALTERACAO_SENHA"))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear revogacao de sessoes sem usuario responsavel")
    void deveBloquearRevogacaoDeSessoesSemUsuarioResponsavel() {
        assertThatThrownBy(() -> service.revogarSessoesDoUsuario(1L, null, "ALTERACAO_SENHA"))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Usuario responsavel obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear revogacao de sessoes sem motivo")
    void deveBloquearRevogacaoDeSessoesSemMotivo() {
        assertThatThrownBy(() -> service.revogarSessoesDoUsuario(1L, 99L, " "))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Motivo da revogacao obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear revogacao com refresh token nulo")
    void deveBloquearRevogacaoComRefreshTokenNulo() {
        assertThatThrownBy(() -> service.revogarSessao(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Refresh token obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear revogacao com refresh token em branco")
    void deveBloquearRevogacaoComRefreshTokenEmBranco() {
        assertThatThrownBy(() -> service.revogarSessao(" "))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Refresh token obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear revogacao com refresh token invalido")
    void deveBloquearRevogacaoComRefreshTokenInvalido() {
        String refreshToken = "refresh-token-invalido";
        String refreshTokenHash = service.gerarHash(refreshToken);

        when(repository.findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.revogarSessao(refreshToken))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token invalido.");
    }

    @Test
    @DisplayName("Deve bloquear renovacao com refresh token nulo")
    void deveBloquearRenovacaoComRefreshTokenNulo() {
        assertThatThrownBy(() -> service.renovarSessao(null))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Refresh token obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear renovacao com refresh token em branco")
    void deveBloquearRenovacaoComRefreshTokenEmBranco() {
        assertThatThrownBy(() -> service.renovarSessao(" "))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Refresh token obrigatorio.");
    }

    @Test
    @DisplayName("Deve bloquear renovacao com refresh token invalido")
    void deveBloquearRenovacaoComRefreshTokenInvalido() {
        String refreshToken = "refresh-token-invalido";
        String refreshTokenHash = service.gerarHash(refreshToken);

        when(repository.findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.renovarSessao(refreshToken))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token invalido.");
    }

    @Test
    @DisplayName("Deve bloquear renovacao com refresh token expirado")
    void deveBloquearRenovacaoComRefreshTokenExpirado() {
        var usuario = criarUsuario(1L, "admin@futuro.com");
        String refreshToken = "refresh-token-expirado";
        String refreshTokenHash = service.gerarHash(refreshToken);

        var sessao = new UsuarioSessaoModel(
                usuario,
                refreshTokenHash,
                "jti-antigo",
                LocalDateTime.now().minusMinutes(1),
                "127.0.0.1",
                "Postman"
        );

        when(repository.findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO))
                .thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> service.renovarSessao(refreshToken))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessage("Refresh token expirado ou revogado.");
    }

    @Test
    @DisplayName("Deve gerar refresh tokens diferentes")
    void deveGerarRefreshTokensDiferentes() {
        var usuario = criarUsuario(1L, "admin@futuro.com");

        String primeiroToken = service.criarSessao(usuario, "jti-1", "127.0.0.1", "Postman");
        String segundoToken = service.criarSessao(usuario, "jti-2", "127.0.0.1", "Postman");

        assertThat(primeiroToken).isNotEqualTo(segundoToken);
    }

    @Test
    @DisplayName("Deve gerar o mesmo hash para o mesmo refresh token")
    void deveGerarMesmoHashParaMesmoRefreshToken() {
        String primeiroHash = service.gerarHash("refresh-token-teste");
        String segundoHash = service.gerarHash("refresh-token-teste");

        assertThat(primeiroHash).isEqualTo(segundoHash);
    }

    @Test
    @DisplayName("Deve gerar hashes diferentes para refresh tokens diferentes")
    void deveGerarHashesDiferentesParaRefreshTokensDiferentes() {
        String primeiroHash = service.gerarHash("refresh-token-1");
        String segundoHash = service.gerarHash("refresh-token-2");

        assertThat(primeiroHash).isNotEqualTo(segundoHash);
    }

    @Test
    @DisplayName("Deve bloquear configuracao sem expiracao do refresh token")
    void deveBloquearConfiguracaoSemExpiracaoDoRefreshToken() {
        ReflectionTestUtils.setField(service, "refreshTokenExpirationDays", null);

        assertThatThrownBy(() -> service.validarConfiguracao())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("api.security.refresh-token.expiration-days deve ser configurado com no minimo 1 dia");
    }

    @Test
    @DisplayName("Deve aceitar configuracao valida de expiracao do refresh token")
    void deveAceitarConfiguracaoValidaDeExpiracaoDoRefreshToken() {
        ReflectionTestUtils.setField(service, "refreshTokenExpirationDays", 7L);

        service.validarConfiguracao();
    }

    @Test
    @DisplayName("Deve retornar true quando access token estiver ativo")
    void deveRetornarTrueQuandoAccessTokenEstiverAtivo() {
        when(repository.existsByAccessTokenJtiAndStatus("jti-ativo", StatusEnum.ATIVO))
                .thenReturn(true);

        boolean resultado = service.accessTokenEstaAtivo("jti-ativo");

        assertThat(resultado).isTrue();

        verify(repository).existsByAccessTokenJtiAndStatus("jti-ativo", StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar false quando access token nao estiver ativo")
    void deveRetornarFalseQuandoAccessTokenNaoEstiverAtivo() {
        when(repository.existsByAccessTokenJtiAndStatus("jti-inativo", StatusEnum.ATIVO))
                .thenReturn(false);

        boolean resultado = service.accessTokenEstaAtivo("jti-inativo");

        assertThat(resultado).isFalse();

        verify(repository).existsByAccessTokenJtiAndStatus("jti-inativo", StatusEnum.ATIVO);
    }

    @Test
    @DisplayName("Deve retornar false quando jti nao for informado")
    void deveRetornarFalseQuandoJtiNaoForInformado() {
        boolean resultadoNulo = service.accessTokenEstaAtivo(null);
        boolean resultadoBranco = service.accessTokenEstaAtivo(" ");

        assertThat(resultadoNulo).isFalse();
        assertThat(resultadoBranco).isFalse();
    }
    
    private UsuarioSessaoModel criarSessao(UsuarioModel usuario, String refreshTokenHash, String accessTokenJti) {
        return new UsuarioSessaoModel(
                usuario,
                refreshTokenHash,
                accessTokenJti,
                LocalDateTime.now().plusDays(7),
                "127.0.0.1",
                "Postman"
        );
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "Senha@123"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}