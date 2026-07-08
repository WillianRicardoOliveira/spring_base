package com.empresa.erp.core.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class TokenSecurityTest {

    private static final String SECRET = "segredo-super-seguro-para-testes";
    private static final String ISSUER = "erp-test";

    private TokenSecurity tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenSecurity();

        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        ReflectionTestUtils.setField(tokenService, "issuer", ISSUER);
    }

    @Test
    @DisplayName("Deve gerar token JWT com issuer, subject, id e expiracao")
    void deveGerarTokenJwtComIssuerSubjectIdEExpiracao() {
        var usuario = criarUsuario(1L, "usuario@teste.com");

        var token = tokenService.gerarToken(usuario);

        var jwt = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer(ISSUER)
                .build()
                .verify(token);

        assertThat(jwt.getSubject()).isEqualTo("usuario@teste.com");
        assertThat(jwt.getClaim("id").asLong()).isEqualTo(1L);
        assertThat(jwt.getExpiresAt().toInstant()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Deve retornar subject de token JWT valido")
    void deveRetornarSubjectDeTokenJwtValido() {
        var token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject("usuario@teste.com")
                .withClaim("id", 1L)
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(SECRET));

        var subject = tokenService.getSubject(token);

        assertThat(subject).isEqualTo("usuario@teste.com");
    }

    @Test
    @DisplayName("Deve bloquear token JWT invalido")
    void deveBloquearTokenJwtInvalido() {
        assertThatThrownBy(() -> tokenService.getSubject("token-invalido"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token JWT invalido ou expirado");
    }

    @Test
    @DisplayName("Deve bloquear token JWT com issuer invalido")
    void deveBloquearTokenJwtComIssuerInvalido() {
        var token = JWT.create()
                .withIssuer("outro-issuer")
                .withSubject("usuario@teste.com")
                .withClaim("id", 1L)
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> tokenService.getSubject(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token JWT invalido ou expirado");
    }

    @Test
    @DisplayName("Deve bloquear token JWT expirado")
    void deveBloquearTokenJwtExpirado() {
        var token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject("usuario@teste.com")
                .withClaim("id", 1L)
                .withExpiresAt(Instant.now().minusSeconds(60))
                .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> tokenService.getSubject(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token JWT invalido ou expirado");
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}