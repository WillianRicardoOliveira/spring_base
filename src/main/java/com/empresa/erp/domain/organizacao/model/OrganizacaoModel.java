package com.empresa.erp.domain.organizacao.model;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "organizacao")
@Entity(name = "OrganizacaoModel")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(
        of = "id",
        callSuper = false
)
public class OrganizacaoModel
        extends AuditoriaModel {

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

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public OrganizacaoModel(
            String nome
    ) {
        this.nome = normalizarNome(nome);
        this.status = StatusEnum.ATIVO;
    }

    public void atualizarNome(
            String nome
    ) {
        this.nome = normalizarNome(nome);
    }

    public void inativar() {
        this.status = StatusEnum.INATIVO;
    }

    public void reativar() {
        this.status = StatusEnum.ATIVO;
    }

    public void remover(
            Long idUsuario
    ) {
        this.status = StatusEnum.REMOVIDO;

        registrarRemocao(idUsuario);
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
}