package com.empresa.erp.domain.acesso.usuarioSessao.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.empresa.erp.core.exception.RefreshTokenException;
import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.record.TokenJwtSecurity;
import com.empresa.erp.domain.acesso.usuarioSessao.model.UsuarioSessaoModel;
import com.empresa.erp.domain.acesso.usuarioSessao.repository.UsuarioSessaoRepository;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioSessaoService {

    private static final int TAMANHO_REFRESH_TOKEN_BYTES = 64;
    private static final long TEMPO_MINIMO_REFRESH_TOKEN_DIAS = 1;
    private static final Long ID_USUARIO_SISTEMA = 0L;
    private static final String MOTIVO_REFRESH_TOKEN_EXPIRADO = "REFRESH_TOKEN_EXPIRADO";

    private final UsuarioSessaoRepository repository;
    private final TokenSecurity tokenSecurity;

    @Value("${api.security.refresh-token.expiration-days}")
    private Long refreshTokenExpirationDays;

    @PostConstruct
    void validarConfiguracao() {
        if (refreshTokenExpirationDays == null || refreshTokenExpirationDays < TEMPO_MINIMO_REFRESH_TOKEN_DIAS) {
            throw new IllegalStateException(
                    "api.security.refresh-token.expiration-days deve ser configurado com no minimo 1 dia"
            );
        }
    }

    @Transactional
    public String criarSessao(
            UsuarioModel usuario,
            String accessTokenJti,
            String ip,
            String userAgent
    ) {
        String refreshToken = gerarRefreshToken();
        String refreshTokenHash = gerarHash(refreshToken);

        var sessao = new UsuarioSessaoModel(
                usuario,
                refreshTokenHash,
                accessTokenJti,
                LocalDateTime.now().plusDays(refreshTokenExpirationDays),
                ip,
                userAgent
        );

        repository.save(sessao);

        return refreshToken;
    }

    @Transactional
    public TokenJwtSecurity renovarSessao(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new ValidacaoException("Refresh token obrigatorio.");
        }

        String refreshTokenHash = gerarHash(refreshToken);

        UsuarioSessaoModel sessao = repository.findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO)
                .orElseThrow(() -> new RefreshTokenException("Refresh token invalido."));

        if (!sessao.estaAtiva()) {
            throw new RefreshTokenException("Refresh token expirado ou revogado.");
        }

        var tokenGerado = tokenSecurity.gerarTokenComJti(sessao.getUsuario());

        String novoRefreshToken = gerarRefreshToken();
        String novoRefreshTokenHash = gerarHash(novoRefreshToken);

        sessao.atualizarRefreshToken(
                novoRefreshTokenHash,
                tokenGerado.jti(),
                LocalDateTime.now().plusDays(refreshTokenExpirationDays)
        );

        return new TokenJwtSecurity(tokenGerado.token(), novoRefreshToken);
    }

    @Transactional
    public void revogarSessoesDoUsuario(Long idUsuario, Long idUsuarioResponsavel, String motivo) {
        if (idUsuario == null) {
            throw new ValidacaoException("Usuario obrigatorio.");
        }

        if (idUsuarioResponsavel == null) {
            throw new ValidacaoException("Usuario responsavel obrigatorio.");
        }

        if (!StringUtils.hasText(motivo)) {
            throw new ValidacaoException("Motivo da revogacao obrigatorio.");
        }

        var sessoes = repository.findAllByUsuarioIdAndStatus(idUsuario, StatusEnum.ATIVO);

        sessoes.forEach(sessao -> sessao.revogar(idUsuarioResponsavel, motivo));
    }

    @Transactional
    public void revogarSessoesExpiradas() {
        var sessoes = repository.findAllByExpiraEmBeforeAndStatus(LocalDateTime.now(), StatusEnum.ATIVO);

        sessoes.forEach(sessao -> sessao.revogar(ID_USUARIO_SISTEMA, MOTIVO_REFRESH_TOKEN_EXPIRADO));
    }

    @Transactional
    public void revogarSessao(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new ValidacaoException("Refresh token obrigatorio.");
        }

        String refreshTokenHash = gerarHash(refreshToken);

        UsuarioSessaoModel sessao = repository.findByRefreshTokenHashAndStatus(refreshTokenHash, StatusEnum.ATIVO)
                .orElseThrow(() -> new RefreshTokenException("Refresh token invalido."));

        sessao.revogar(sessao.getUsuario().getId(), "LOGOUT");
    }
    
    @Transactional(readOnly = true)
    public boolean accessTokenEstaAtivo(String accessTokenJti) {
        if (!StringUtils.hasText(accessTokenJti)) {
            return false;
        }

        return repository.existsByAccessTokenJtiAndStatus(accessTokenJti, StatusEnum.ATIVO);
    }

    public String gerarHash(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);
        } catch (Exception exception) {
            throw new RuntimeException("Erro ao gerar hash do refresh token", exception);
        }
    }

    private String gerarRefreshToken() {
        byte[] bytes = new byte[TAMANHO_REFRESH_TOKEN_BYTES];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}