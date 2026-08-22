package com.empresa.erp.domain.acesso.usuarioPerfil.model;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "usuario_perfil")
@Entity(name = "UsuarioPerfilModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class UsuarioPerfilModel
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
            name = "id_usuario_organizacao",
            nullable = false
    )
    private UsuarioOrganizacaoModel
            usuarioOrganizacao;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_perfil",
            nullable = false
    )
    private PerfilModel perfil;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public UsuarioPerfilModel(
            UsuarioOrganizacaoModel usuarioOrganizacao,
            PerfilModel perfil
    ) {
        this.usuarioOrganizacao =
                usuarioOrganizacao;

        this.perfil =
                perfil;

        this.status =
                StatusEnum.ATIVO;
    }

    public void remover(
            Long idUsuario
    ) {
        this.status =
                StatusEnum.REMOVIDO;

        registrarRemocao(idUsuario);
    }
}