package com.empresa.erp.domain.acesso.usuarioSessao.model;

import java.time.LocalDateTime;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario_sessao")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsuarioSessaoModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    @Column(name = "access_token_jti")
    private String accessTokenJti;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "revogado_em")
    private LocalDateTime revogadoEm;

    @Column(name = "revogado_por")
    private Long revogadoPor;

    @Column(name = "motivo_revogacao")
    private String motivoRevogacao;

    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    public UsuarioSessaoModel(
            UsuarioModel usuario,
            String refreshTokenHash,
            String accessTokenJti,
            LocalDateTime expiraEm,
            String ip,
            String userAgent
    ) {
        this.usuario = usuario;
        this.refreshTokenHash = refreshTokenHash;
        this.accessTokenJti = accessTokenJti;
        this.expiraEm = expiraEm;
        this.ip = ip;
        this.userAgent = userAgent;
        this.status = StatusEnum.ATIVO;
    }

    public void revogar(Long idUsuario, String motivoRevogacao) {
        this.status = StatusEnum.INATIVO;
        this.revogadoEm = LocalDateTime.now();
        this.revogadoPor = idUsuario;
        this.motivoRevogacao = motivoRevogacao;
    }

    public boolean estaAtiva() {
        return StatusEnum.ATIVO.equals(status)
                && revogadoEm == null
                && expiraEm.isAfter(LocalDateTime.now());
    }

    public void atualizarRefreshToken(String refreshTokenHash, String accessTokenJti, LocalDateTime expiraEm) {
        this.refreshTokenHash = refreshTokenHash;
        this.accessTokenJti = accessTokenJti;
        this.expiraEm = expiraEm;
        this.revogadoEm = null;
        this.revogadoPor = null;
        this.motivoRevogacao = null;
        this.status = StatusEnum.ATIVO;
    }
}