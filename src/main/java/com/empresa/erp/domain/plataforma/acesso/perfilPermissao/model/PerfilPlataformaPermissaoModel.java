package com.empresa.erp.domain.plataforma.acesso.perfilPermissao.model;

import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.acesso.perfil.model.PerfilPlataformaModel;

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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "perfil_plataforma_permissao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_perfil_plataforma_permissao_perfil_permissao",
                        columnNames = {
                                "id_perfil_plataforma",
                                "id_permissao"
                        }
                )
        }
)
@Entity(name = "PerfilPlataformaPermissaoModel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class PerfilPlataformaPermissaoModel
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
            name = "id_perfil_plataforma",
            nullable = false
    )
    private PerfilPlataformaModel perfil;

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

    public PerfilPlataformaPermissaoModel(
            PerfilPlataformaModel perfil,
            PermissaoModel permissao
    ) {
        this.perfil = perfil;
        this.permissao = permissao;
        this.status = StatusEnum.ATIVO;
    }
}