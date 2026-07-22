package com.empresa.erp.core.security.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.empresa.erp.core.security.record.TokenGeradoSecurity;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

import jakarta.annotation.PostConstruct;

@Service
public class TokenSecurity {

    private static final int TAMANHO_MINIMO_SECRET = 32;
    private static final long TEMPO_MINIMO_EXPIRACAO_MINUTOS = 5;

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    @Value("${api.security.token.expiration-minutes}")
    private Long expirationMinutes;

    @PostConstruct
    void validarConfiguracao() {
        validarSecret();
        validarIssuer();
        validarExpiracao();
    }

    private void validarSecret() {
        if (!StringUtils.hasText(secret) || secret.length() < TAMANHO_MINIMO_SECRET) {
            throw new IllegalStateException(
                    "JWT_SECRET/api.security.token.secret deve ser configurado com no minimo 32 caracteres"
            );
        }
    }

    private void validarIssuer() {
        if (!StringUtils.hasText(issuer)) {
            throw new IllegalStateException(
                    "JWT_ISSUER/api.security.token.issuer deve ser configurado"
            );
        }

        if (issuer.contains("\"") || issuer.contains("'")) {
            throw new IllegalStateException(
                    "JWT_ISSUER/api.security.token.issuer nao deve conter aspas"
            );
        }
    }

    private void validarExpiracao() {
        if (expirationMinutes == null || expirationMinutes < TEMPO_MINIMO_EXPIRACAO_MINUTOS) {
            throw new IllegalStateException(
                    "JWT_EXPIRATION_MINUTES/api.security.token.expiration-minutes deve ser configurado com no minimo 5 minutos"
            );
        }
    }

    public String gerarToken(UsuarioModel usuario) {
        return gerarTokenComJti(usuario).token();
    }

    public TokenGeradoSecurity gerarTokenComJti(UsuarioModel usuario) {
        try {
            String jti = UUID.randomUUID().toString();

            Algorithm algoritmo = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer(issuer)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withJWTId(jti)
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);

            return new TokenGeradoSecurity(token, jti);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        return verificarToken(tokenJWT).getSubject();
    }

    public String getJti(String tokenJWT) {
        return verificarToken(tokenJWT).getId();
    }

    private DecodedJWT verificarToken(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);

            return JWT.require(algoritmo)
                    .withIssuer(issuer)
                    .build()
                    .verify(tokenJWT);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT invalido ou expirado");
        }
    }

    private Instant dataExpiracao() {
        return Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES);
    }
}