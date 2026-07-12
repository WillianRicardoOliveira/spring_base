package com.empresa.erp.core.security.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

import jakarta.annotation.PostConstruct;

@Service
public class TokenSecurity {

    private static final int TAMANHO_MINIMO_SECRET = 32;

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.issuer}")
    private String issuer;

    @PostConstruct
    void validarConfiguracao() {
        validarSecret();
        validarIssuer();
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

    public String gerarToken(UsuarioModel usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("erro ao gerar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer(issuer)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT invalido ou expirado");
        }
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}