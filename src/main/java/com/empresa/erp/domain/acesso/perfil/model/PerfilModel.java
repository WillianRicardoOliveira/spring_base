package com.empresa.erp.domain.acesso.perfil.model;

import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    
    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public PerfilModel(PerfilRecord dados) {
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.status = StatusEnum.ATIVO;
    }

    public void atualizar(AtualizaPerfilRecord dados) {
        this.nome = dados.nome();
        this.descricao = dados.descricao();
    }

    public void inativar() {
        this.status = StatusEnum.INATIVO;
    }

    public void remover(Long idUsuario) {
        this.status = StatusEnum.REMOVIDO;
        registrarRemocao(idUsuario);
    }
    
}