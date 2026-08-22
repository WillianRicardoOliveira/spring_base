package com.empresa.erp.domain.plataforma.acesso.usuarioPerfil.model;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.acesso.perfil.model.PerfilPlataformaModel;
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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(
        name = "usuario_perfil_plataforma",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_perfil_plataforma_usuario_perfil",
                        columnNames = {
                                "id_usuario",
                                "id_perfil_plataforma"
                        }
                )
        }
)
@Entity(name = "UsuarioPerfilPlataformaModel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class UsuarioPerfilPlataformaModel
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
            name = "id_usuario",
            nullable = false
    )
    private UsuarioModel usuario;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_perfil_plataforma",
            nullable = false
    )
    private PerfilPlataformaModel perfil;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public UsuarioPerfilPlataformaModel(
            UsuarioModel usuario,
            PerfilPlataformaModel perfil
    ) {
        this.usuario = usuario;
        this.perfil = perfil;
        this.status = StatusEnum.ATIVO;
    }
}