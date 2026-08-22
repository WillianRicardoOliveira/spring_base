package com.empresa.erp.domain.acesso.perfilPermissao.model;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;

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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "perfil_permissao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_perfil_permissao_perfil_permissao",
                        columnNames = {
                                "id_perfil",
                                "id_permissao"
                        }
                )
        }
)
@Entity(name = "PerfilPermissaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class PerfilPermissaoModel
        extends AuditoriaModel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_perfil",
            nullable = false
    )
    private PerfilModel perfil;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_permissao",
            nullable = false
    )
    private PermissaoModel permissao;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public PerfilPermissaoModel(
            PerfilModel perfil,
            PermissaoModel permissao
    ) {
        this.perfil = perfil;
        this.permissao = permissao;
        this.status = StatusEnum.ATIVO;
    }

    public void reativar() {
        this.status = StatusEnum.ATIVO;

        limparRemocao();
    }

    public void remover(
            Long idUsuario
    ) {
        this.status = StatusEnum.REMOVIDO;

        registrarRemocao(idUsuario);
    }
}