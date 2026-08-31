package com.empresa.erp.domain.plataforma.organizacao.convite.model;

import java.time.LocalDateTime;
import java.util.Locale;

import com.empresa.erp.domain.base.model.AuditoriaModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "convite_organizacao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_convite_organizacao_token_hash",
                        columnNames = "token_hash"
                ),
                @UniqueConstraint(
                        name = "uk_convite_organizacao_email_pendente",
                        columnNames = "email_pendente"
                )
        }
)
@Entity(name = "ConviteOrganizacaoModel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class ConviteOrganizacaoModel
        extends AuditoriaModel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "nome_organizacao",
            nullable = false,
            length = 100
    )
    private String nomeOrganizacao;

    @Column(
            name = "email_administrador",
            nullable = false,
            length = 100
    )
    private String emailAdministrador;

    @Getter(AccessLevel.NONE)
    @Column(
            name = "email_pendente",
            length = 100
    )
    private String emailPendente;

    @Column(
            name = "token_hash",
            nullable = false,
            length = 64
    )
    private String tokenHash;

    @Column(
            name = "expira_em",
            nullable = false
    )
    private LocalDateTime expiraEm;

    @Column(name = "aceito_em")
    private LocalDateTime aceitoEm;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private StatusConviteOrganizacaoEnum status;

    public ConviteOrganizacaoModel(
            String nomeOrganizacao,
            String emailAdministrador,
            String tokenHash,
            LocalDateTime expiraEm
    ) {
        this.nomeOrganizacao =
                normalizarNome(nomeOrganizacao);

        this.emailAdministrador =
                normalizarEmail(emailAdministrador);

        this.emailPendente =
                this.emailAdministrador;

        this.tokenHash = tokenHash;
        this.expiraEm = expiraEm;
        this.status =
                StatusConviteOrganizacaoEnum.PENDENTE;
    }

    public boolean podeSerAceito(
            LocalDateTime referencia
    ) {
        return StatusConviteOrganizacaoEnum.PENDENTE
                .equals(status)
                && expiraEm.isAfter(referencia);
    }

    public void renovar(
            String novoTokenHash,
            LocalDateTime novaExpiracao
    ) {
        this.tokenHash = novoTokenHash;
        this.expiraEm = novaExpiracao;
    }

    public void aceitar(
            LocalDateTime dataAceitacao
    ) {
        this.status =
                StatusConviteOrganizacaoEnum.ACEITO;

        this.aceitoEm = dataAceitacao;
        this.emailPendente = null;
    }

    public void revogar() {
        this.status =
                StatusConviteOrganizacaoEnum.REVOGADO;

        this.emailPendente = null;
    }

    private String normalizarNome(
            String nome
    ) {
        return nome == null
                ? null
                : nome
                        .trim()
                        .replaceAll("\\s+", " ");
    }

    private String normalizarEmail(
            String email
    ) {
        return email == null
                ? null
                : email
                        .trim()
                        .toLowerCase(Locale.ROOT);
    }
}