package com.empresa.erp.domain.acesso.perfil.model;

import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;

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

@Table(name = "perfil")
@Entity(name = "PerfilModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class PerfilModel extends AuditoriaModel {

    private static final String NOME_ADMINISTRADOR =
            "Administrador";

    private static final String DESCRICAO_ADMINISTRADOR =
            "Perfil com acesso total a organizacao";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "id_organizacao",
            nullable = false
    )
    private OrganizacaoModel organizacao;

    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_sistema",
            length = 50
    )
    private TipoPerfilSistemaEnum tipoSistema;

    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public PerfilModel(
            OrganizacaoModel organizacao,
            PerfilRecord dados
    ) {
        this.organizacao =
                organizacao;

        this.nome =
                dados.nome();

        this.descricao =
                dados.descricao();

        this.tipoSistema =
                null;

        this.status =
                StatusEnum.ATIVO;
    }

    public static PerfilModel criarAdministradorSistema(
            OrganizacaoModel organizacao
    ) {
        var perfil = new PerfilModel();

        perfil.organizacao =
                organizacao;

        perfil.nome =
                NOME_ADMINISTRADOR;

        perfil.descricao =
                DESCRICAO_ADMINISTRADOR;

        perfil.tipoSistema =
                TipoPerfilSistemaEnum.ADMINISTRADOR;

        perfil.status =
                StatusEnum.ATIVO;

        return perfil;
    }

    public void atualizar(
            AtualizaPerfilRecord dados
    ) {
        this.nome =
                dados.nome();

        this.descricao =
                dados.descricao();
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

    public boolean isSistema() {
        return tipoSistema != null;
    }

    public boolean isAdministradorSistema() {
        return TipoPerfilSistemaEnum.ADMINISTRADOR
                .equals(tipoSistema);
    }
}