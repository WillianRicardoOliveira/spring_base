package com.empresa.erp.domain.acesso.usuarioEmpresa.model;

import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;

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

@Table(name = "usuario_empresa")
@Entity(name = "UsuarioEmpresaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class UsuarioEmpresaModel
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
            name = "id_empresa",
            nullable = false
    )
    private EmpresaModel empresa;

    @Column(
            name = "todas_subsidiarias",
            nullable = false
    )
    private Boolean todasSubsidiarias;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public UsuarioEmpresaModel(
            UsuarioOrganizacaoModel usuarioOrganizacao,
            EmpresaModel empresa,
            Boolean todasSubsidiarias
    ) {
        this.usuarioOrganizacao =
                usuarioOrganizacao;

        this.empresa =
                empresa;

        this.todasSubsidiarias =
                todasSubsidiarias;

        this.status =
                StatusEnum.ATIVO;
    }

    public void atualizar(
            AtualizaUsuarioEmpresaRecord dados
    ) {
        this.todasSubsidiarias =
                dados.todasSubsidiarias();
    }

    public void inativar() {
        this.status =
                StatusEnum.INATIVO;
    }

    public void remover(
            Long idUsuario
    ) {
        this.status =
                StatusEnum.REMOVIDO;

        registrarRemocao(idUsuario);
    }
}