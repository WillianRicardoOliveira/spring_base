package com.empresa.erp.domain.acesso.usuarioSessao.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class UsuarioSessaoModelTest {

    @Test
    @DisplayName("Deve criar sessao ativa")
    void deveCriarSessaoAtiva() {
        var usuario = criarUsuario(1L, "admin@futuro.com");
        var expiraEm = LocalDateTime.now().plusHours(8);

        var sessao = new UsuarioSessaoModel(
                usuario,
                "hash-refresh",
                "jti-access",
                expiraEm,
                "127.0.0.1",
                "Postman"
        );

        assertThat(sessao.getUsuario()).isEqualTo(usuario);
        assertThat(sessao.getRefreshTokenHash()).isEqualTo("hash-refresh");
        assertThat(sessao.getAccessTokenJti()).isEqualTo("jti-access");
        assertThat(sessao.getExpiraEm()).isEqualTo(expiraEm);
        assertThat(sessao.getIp()).isEqualTo("127.0.0.1");
        assertThat(sessao.getUserAgent()).isEqualTo("Postman");
        assertThat(sessao.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(sessao.getRevogadoEm()).isNull();
        assertThat(sessao.getRevogadoPor()).isNull();
        assertThat(sessao.getMotivoRevogacao()).isNull();
        assertThat(sessao.estaAtiva()).isTrue();
    }

    @Test
    @DisplayName("Deve revogar sessao")
    void deveRevogarSessao() {
        var sessao = criarSessaoAtiva();

        sessao.revogar(10L, "LOGOUT");

        assertThat(sessao.getStatus()).isEqualTo(StatusEnum.INATIVO);
        assertThat(sessao.getRevogadoEm()).isNotNull();
        assertThat(sessao.getRevogadoPor()).isEqualTo(10L);
        assertThat(sessao.getMotivoRevogacao()).isEqualTo("LOGOUT");
        assertThat(sessao.estaAtiva()).isFalse();
    }

    @Test
    @DisplayName("Deve indicar sessao expirada como inativa")
    void deveIndicarSessaoExpiradaComoInativa() {
        var usuario = criarUsuario(1L, "admin@futuro.com");

        var sessao = new UsuarioSessaoModel(
                usuario,
                "hash-refresh",
                "jti-access",
                LocalDateTime.now().minusMinutes(1),
                "127.0.0.1",
                "Postman"
        );

        assertThat(sessao.estaAtiva()).isFalse();
    }

    @Test
    @DisplayName("Deve indicar sessao revogada como inativa")
    void deveIndicarSessaoRevogadaComoInativa() {
        var sessao = criarSessaoAtiva();

        sessao.revogar(10L, "LOGOUT");

        assertThat(sessao.estaAtiva()).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar refresh token e manter sessao ativa")
    void deveAtualizarRefreshTokenEManterSessaoAtiva() {
        var sessao = criarSessaoAtiva();
        var novaExpiracao = LocalDateTime.now().plusHours(8);

        sessao.revogar(10L, "ROTACAO_REFRESH_TOKEN");
        sessao.atualizarRefreshToken("novo-hash-refresh", "novo-jti-access", novaExpiracao);

        assertThat(sessao.getRefreshTokenHash()).isEqualTo("novo-hash-refresh");
        assertThat(sessao.getAccessTokenJti()).isEqualTo("novo-jti-access");
        assertThat(sessao.getExpiraEm()).isEqualTo(novaExpiracao);
        assertThat(sessao.getStatus()).isEqualTo(StatusEnum.ATIVO);
        assertThat(sessao.getRevogadoEm()).isNull();
        assertThat(sessao.getRevogadoPor()).isNull();
        assertThat(sessao.getMotivoRevogacao()).isNull();
        assertThat(sessao.estaAtiva()).isTrue();
    }

    private UsuarioSessaoModel criarSessaoAtiva() {
        return new UsuarioSessaoModel(
                criarUsuario(1L, "admin@futuro.com"),
                "hash-refresh",
                "jti-access",
                LocalDateTime.now().plusHours(8),
                "127.0.0.1",
                "Postman"
        );
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(
                new UsuarioRecord(email, "Senha@123"),
                "senha-criptografada"
        );

        ReflectionTestUtils.setField(usuario, "id", id);

        return usuario;
    }
}