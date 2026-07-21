package com.empresa.erp.core.security.record;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TokenJwtSecurityTest {

    @Test
    @DisplayName("Deve armazenar token JWT")
    void deveArmazenarTokenJwt() {
        var dados = new TokenJwtSecurity("jwt-token");

        assertThat(dados.token()).isEqualTo("jwt-token");
        assertThat(dados.refreshToken()).isNull();
    }

    @Test
    @DisplayName("Deve armazenar token JWT e refresh token")
    void deveArmazenarTokenJwtERefreshToken() {
        var dados = new TokenJwtSecurity("jwt-token", "refresh-token");

        assertThat(dados.token()).isEqualTo("jwt-token");
        assertThat(dados.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("Deve comparar tokens pelo valor")
    void deveCompararTokensPeloValor() {
        var token1 = new TokenJwtSecurity("jwt-token", "refresh-token");
        var token2 = new TokenJwtSecurity("jwt-token", "refresh-token");

        assertThat(token1).isEqualTo(token2);
        assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
    }

    @Test
    @DisplayName("Nao deve expor token nem refresh token no toString")
    void naoDeveExporTokenNemRefreshTokenNoToString() {
        var dados = new TokenJwtSecurity("jwt-token", "refresh-token");

        assertThat(dados.toString()).doesNotContain("jwt-token");
        assertThat(dados.toString()).doesNotContain("refresh-token");
        assertThat(dados.toString()).contains("****");
    }
}