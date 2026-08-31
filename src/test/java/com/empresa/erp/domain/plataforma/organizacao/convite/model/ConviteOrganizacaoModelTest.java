package com.empresa.erp.domain.plataforma.organizacao.convite.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ConviteOrganizacaoModelTest {

    private static final LocalDateTime AGORA =
            LocalDateTime.of(
                    2026,
                    8,
                    23,
                    10,
                    0
            );

    @Test
    @DisplayName(
            "Deve criar convite pendente normalizado"
    )
    void deveCriarConvitePendenteNormalizado() {
        var expiraEm =
                AGORA.plusHours(48);

        var convite =
                new ConviteOrganizacaoModel(
                        "  Organização   Principal  ",
                        "  ADMIN@TESTE.COM  ",
                        "hash-token",
                        expiraEm
                );

        assertThat(convite.getNomeOrganizacao())
                .isEqualTo(
                        "Organização Principal"
                );

        assertThat(convite.getEmailAdministrador())
                .isEqualTo(
                        "admin@teste.com"
                );

        assertThat(convite.getTokenHash())
                .isEqualTo("hash-token");

        assertThat(convite.getExpiraEm())
                .isEqualTo(expiraEm);

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(convite.getAceitoEm())
                .isNull();

        assertThat(emailPendente(convite))
                .isEqualTo(
                        "admin@teste.com"
                );
    }

    @Test
    @DisplayName(
            "Deve permitir aceite antes da expiração"
    )
    void devePermitirAceiteAntesDaExpiracao() {
        var convite =
                criarConvite(
                        AGORA.plusHours(1)
                );

        assertThat(
                convite.podeSerAceito(AGORA)
        ).isTrue();
    }

    @Test
    @DisplayName(
            "Não deve permitir aceite exatamente "
                    + "na expiração"
    )
    void naoDevePermitirAceiteExatamenteNaExpiracao() {
        var convite =
                criarConvite(
                        AGORA
                );

        assertThat(
                convite.podeSerAceito(AGORA)
        ).isFalse();
    }

    @Test
    @DisplayName(
            "Não deve permitir aceite depois da expiração"
    )
    void naoDevePermitirAceiteDepoisDaExpiracao() {
        var convite =
                criarConvite(
                        AGORA.minusSeconds(1)
                );

        assertThat(
                convite.podeSerAceito(AGORA)
        ).isFalse();
    }

    @Test
    @DisplayName(
            "Deve renovar token e expiração"
    )
    void deveRenovarTokenEExpiracao() {
        var convite =
                criarConvite(
                        AGORA.minusHours(1)
                );

        var novaExpiracao =
                AGORA.plusHours(48);

        convite.renovar(
                "novo-hash",
                novaExpiracao
        );

        assertThat(convite.getTokenHash())
                .isEqualTo("novo-hash");

        assertThat(convite.getExpiraEm())
                .isEqualTo(novaExpiracao);

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.PENDENTE
                );

        assertThat(emailPendente(convite))
                .isEqualTo(
                        "admin@teste.com"
                );

        assertThat(
                convite.podeSerAceito(AGORA)
        ).isTrue();
    }

    @Test
    @DisplayName(
            "Deve aceitar convite"
    )
    void deveAceitarConvite() {
        var convite =
                criarConvite(
                        AGORA.plusHours(1)
                );

        convite.aceitar(AGORA);

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.ACEITO
                );

        assertThat(convite.getAceitoEm())
                .isEqualTo(AGORA);

        assertThat(emailPendente(convite))
                .isNull();

        assertThat(
                convite.podeSerAceito(
                        AGORA.minusMinutes(1)
                )
        ).isFalse();
    }

    @Test
    @DisplayName(
            "Deve revogar convite"
    )
    void deveRevogarConvite() {
        var convite =
                criarConvite(
                        AGORA.plusHours(1)
                );

        convite.revogar();

        assertThat(convite.getStatus())
                .isEqualTo(
                        StatusConviteOrganizacaoEnum.REVOGADO
                );

        assertThat(convite.getAceitoEm())
                .isNull();

        assertThat(emailPendente(convite))
                .isNull();

        assertThat(
                convite.podeSerAceito(AGORA)
        ).isFalse();
    }

    private ConviteOrganizacaoModel criarConvite(
            LocalDateTime expiraEm
    ) {
        return new ConviteOrganizacaoModel(
                "Organização",
                "admin@teste.com",
                "hash-token",
                expiraEm
        );
    }

    private String emailPendente(
            ConviteOrganizacaoModel convite
    ) {
        return (String)
                ReflectionTestUtils.getField(
                        convite,
                        "emailPendente"
                );
    }
}