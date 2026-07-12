package com.empresa.erp.core.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class TokenSecurityTest {

    private static final String SECRET = "segredo-super-seguro-para-testes";
    private static final String ISSUER = "erp-test";
    private static final Long EXPIRATION_MINUTES = 120L;

    private TokenSecurity tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenSecurity();

        ReflectionTestUtils.setField(tokenService, "secret", SECRET);
        ReflectionTestUtils.setField(tokenService, "issuer", ISSUER);
        ReflectionTestUtils.setField(tokenService, "expirationMinutes", EXPIRATION_MINUTES);

        tokenService.validarConfiguracao();
    }

    @Test
    @DisplayName("Deve validar configuracao com secret, issuer e expiracao seguros")
    void deveValidarConfiguracaoComSecretIssuerEExpiracaoSeguros() {
        assertThat(SECRET).hasSizeGreaterThanOrEqualTo(32);
        assertThat(ISSUER).isNotBlank();
        assertThat(ISSUER).doesNotContain("\"", "'");
        assertThat(EXPIRATION_MINUTES).isGreaterThanOrEqualTo(5L);
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com secret nulo")
    void deveBloquearInicializacaoComSecretNulo() {
        var tokenSecurity = criarTokenSecurity(null, ISSUER, EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_SECRET/api.security.token.secret deve ser configurado com no minimo 32 caracteres");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com secret em branco")
    void deveBloquearInicializacaoComSecretEmBranco() {
        var tokenSecurity = criarTokenSecurity(" ", ISSUER, EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_SECRET/api.security.token.secret deve ser configurado com no minimo 32 caracteres");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com secret fraco")
    void deveBloquearInicializacaoComSecretFraco() {
        var tokenSecurity = criarTokenSecurity("12345678", ISSUER, EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_SECRET/api.security.token.secret deve ser configurado com no minimo 32 caracteres");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com issuer nulo")
    void deveBloquearInicializacaoComIssuerNulo() {
        var tokenSecurity = criarTokenSecurity(SECRET, null, EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_ISSUER/api.security.token.issuer deve ser configurado");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com issuer em branco")
    void deveBloquearInicializacaoComIssuerEmBranco() {
        var tokenSecurity = criarTokenSecurity(SECRET, " ", EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_ISSUER/api.security.token.issuer deve ser configurado");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com issuer contendo aspas duplas")
    void deveBloquearInicializacaoComIssuerContendoAspasDuplas() {
        var tokenSecurity = criarTokenSecurity(SECRET, "\"API futuro\"", EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_ISSUER/api.security.token.issuer nao deve conter aspas");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com issuer contendo aspas simples")
    void deveBloquearInicializacaoComIssuerContendoAspasSimples() {
        var tokenSecurity = criarTokenSecurity(SECRET, "'API futuro'", EXPIRATION_MINUTES);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_ISSUER/api.security.token.issuer nao deve conter aspas");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com expiracao nula")
    void deveBloquearInicializacaoComExpiracaoNula() {
        var tokenSecurity = criarTokenSecurity(SECRET, ISSUER, null);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_EXPIRATION_MINUTES/api.security.token.expiration-minutes deve ser configurado com no minimo 5 minutos");
    }

    @Test
    @DisplayName("Deve bloquear inicializacao com expiracao menor que minimo")
    void deveBloquearInicializacaoComExpiracaoMenorQueMinimo() {
        var tokenSecurity = criarTokenSecurity(SECRET, ISSUER, 4L);

        assertThatThrownBy(tokenSecurity::validarConfiguracao)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("JWT_EXPIRATION_MINUTES/api.security.token.expiration-minutes deve ser configurado com no minimo 5 minutos");
    }

    @Test
    @DisplayName("Deve manter issuer externalizado no application properties")
    void deveManterIssuerExternalizadoNoApplicationProperties() throws Exception {
        var properties = carregarProperties("application.properties");

        assertThat(properties.getProperty("api.security.token.issuer")).isEqualTo("${JWT_ISSUER}");
    }

    @Test
    @DisplayName("Deve configurar issuer local no profile dev")
    void deveConfigurarIssuerLocalNoProfileDev() throws Exception {
        var properties = carregarProperties("application-dev.properties");

        assertThat(properties.getProperty("api.security.token.issuer")).isEqualTo("erp-api-dev");
    }

    @Test
    @DisplayName("Deve manter issuer externalizado no profile prod")
    void deveManterIssuerExternalizadoNoProfileProd() throws Exception {
        var properties = carregarProperties("application-prod.properties");

        assertThat(properties.getProperty("api.security.token.issuer")).isEqualTo("${JWT_ISSUER}");
    }

    @Test
    @DisplayName("Deve manter expiracao externalizada no application properties")
    void deveManterExpiracaoExternalizadaNoApplicationProperties() throws Exception {
        var properties = carregarProperties("application.properties");

        assertThat(properties.getProperty("api.security.token.expiration-minutes")).isEqualTo("${JWT_EXPIRATION_MINUTES}");
    }

    @Test
    @DisplayName("Deve configurar expiracao local no profile dev")
    void deveConfigurarExpiracaoLocalNoProfileDev() throws Exception {
        var properties = carregarProperties("application-dev.properties");

        assertThat(properties.getProperty("api.security.token.expiration-minutes")).isEqualTo("480");
    }

    @Test
    @DisplayName("Deve manter expiracao externalizada no profile prod")
    void deveManterExpiracaoExternalizadaNoProfileProd() throws Exception {
        var properties = carregarProperties("application-prod.properties");

        assertThat(properties.getProperty("api.security.token.expiration-minutes")).isEqualTo("${JWT_EXPIRATION_MINUTES}");
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

    private TokenSecurity criarTokenSecurity(String secret, String issuer, Long expirationMinutes) {
        var tokenSecurity = new TokenSecurity();

        ReflectionTestUtils.setField(tokenSecurity, "secret", secret);
        ReflectionTestUtils.setField(tokenSecurity, "issuer", issuer);
        ReflectionTestUtils.setField(tokenSecurity, "expirationMinutes", expirationMinutes);

        return tokenSecurity;
    }

    private Properties carregarProperties(String arquivo) throws Exception {
        var properties = new Properties();
        var resource = new ClassPathResource(arquivo);

        try (var inputStream = resource.getInputStream()) {
            properties.load(inputStream);
        }

        return properties;
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "Senha@123"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}