package com.empresa.erp.domain.acesso.usuarioOrganizacao.model;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
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
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "usuario_organizacao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_organizacao_usuario_organizacao",
                        columnNames = {
                                "id_usuario",
                                "id_organizacao"
                        }
                )
        }
)
@Entity(name = "UsuarioOrganizacaoModel")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsuarioOrganizacaoModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_organizacao", nullable = false)
    private OrganizacaoModel organizacao;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public UsuarioOrganizacaoModel(
            UsuarioModel usuario,
            OrganizacaoModel organizacao
    ) {
        this.usuario = usuario;
        this.organizacao = organizacao;
        this.status = StatusEnum.ATIVO;
    }

    public void inativar() {
        this.status = StatusEnum.INATIVO;
    }

    public void reativar() {
        this.status = StatusEnum.ATIVO;
    }
}