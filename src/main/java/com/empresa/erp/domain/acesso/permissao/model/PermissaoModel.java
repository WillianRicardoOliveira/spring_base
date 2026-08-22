package com.empresa.erp.domain.acesso.permissao.model;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "permissao")
@Entity(name = "PermissaoModel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PermissaoModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String chave;

    private String descricao;

    private Boolean sistema;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private EscopoPermissaoEnum escopo;

    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;
}