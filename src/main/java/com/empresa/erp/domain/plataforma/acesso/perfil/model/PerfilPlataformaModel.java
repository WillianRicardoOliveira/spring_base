package com.empresa.erp.domain.plataforma.acesso.perfil.model;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.base.model.StatusEnum;

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
        name = "perfil_plataforma",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_perfil_plataforma_tipo_sistema",
                        columnNames = "tipo_sistema"
                )
        }
)
@Entity(name = "PerfilPlataformaModel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class PerfilPlataformaModel
        extends AuditoriaModel {

    private static final String NOME_ADMINISTRADOR =
            "Administrador da plataforma";

    private static final String DESCRICAO_ADMINISTRADOR =
            "Perfil administrativo reservado da plataforma";

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            nullable = false,
            length = 100
    )
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_sistema",
            length = 50
    )
    private TipoPerfilPlataformaSistemaEnum tipoSistema;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public static PerfilPlataformaModel
            criarAdministradorSistema() {
        var perfil =
                new PerfilPlataformaModel();

        perfil.nome = NOME_ADMINISTRADOR;
        perfil.descricao =
                DESCRICAO_ADMINISTRADOR;
        perfil.tipoSistema =
                TipoPerfilPlataformaSistemaEnum.ADMINISTRADOR;
        perfil.status = StatusEnum.ATIVO;

        return perfil;
    }

    public boolean isSistema() {
        return tipoSistema != null;
    }

    public boolean isAdministradorSistema() {
        return TipoPerfilPlataformaSistemaEnum.ADMINISTRADOR
                .equals(tipoSistema);
    }
}