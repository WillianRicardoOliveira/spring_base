package com.empresa.erp.domain.configuracao.empresa.model;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
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

@Table(name = "empresa")
@Entity(name = "EmpresaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class EmpresaModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_organizacao",
            nullable = false
    )
    private OrganizacaoModel organizacao;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public EmpresaModel(
            OrganizacaoModel organizacao,
            EmpresaRecord dados
    ) {
        this.organizacao = organizacao;
        this.nome = normalizarNome(dados.nome());
        this.status = StatusEnum.ATIVO;
    }

    public void atualizar(AtualizaEmpresaRecord dados) {
        this.nome = normalizarNome(dados.nome());
    }

    public void inativar() {
        this.status = StatusEnum.INATIVO;
    }

    public void remover(Long idUsuario) {
        this.status = StatusEnum.REMOVIDO;
        registrarRemocao(idUsuario);
    }

    private String normalizarNome(String nome) {
        return nome == null
                ? null
                : nome.trim().replaceAll("\\s+", " ");
    }
}