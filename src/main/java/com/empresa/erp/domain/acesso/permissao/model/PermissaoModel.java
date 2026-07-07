package com.empresa.erp.domain.acesso.permissao.model;

import com.empresa.erp.domain.acesso.permissao.record.AtualizaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.PermissaoRecord;
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

@Table(name = "permissao")
@Entity(name = "PermissaoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class PermissaoModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String chave;
    private String descricao;

    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public PermissaoModel(PermissaoRecord dados) {
        this.nome = dados.nome();
        this.chave = dados.chave();
        this.descricao = dados.descricao();
        this.status = StatusEnum.ATIVO;
    }

    public void atualizar(AtualizaPermissaoRecord dados) {
        this.nome = dados.nome();
        this.chave = dados.chave();
        this.descricao = dados.descricao();
    }

    public void inativar() {
        this.status = StatusEnum.INATIVO;
    }

    public void remover() {
        this.status = StatusEnum.REMOVIDO;
    }
    
}