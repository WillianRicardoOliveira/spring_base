package com.empresa.erp.domain.acesso.perfilPermissao.model;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.old.StatusEnum;

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

@Table(name = "perfil_permissao")
@Entity(name = "PerfilPermissaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PerfilPermissaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_perfil")
    private PerfilModel perfil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_permissao")
    private PermissaoModel permissao;

    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public PerfilPermissaoModel(PerfilModel perfil, PermissaoModel permissao) {
        this.perfil = perfil;
        this.permissao = permissao;
        this.status = StatusEnum.ATIVO;
    }

    public void remover() {
        this.status = StatusEnum.REMOVIDO;
    }
    
}