package com.empresa.erp.domain.acesso.usuarioSubsidiaria.model;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;

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

@Table(name = "usuario_subsidiaria")
@Entity(name = "UsuarioSubsidiariaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsuarioSubsidiariaModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario_empresa",
            nullable = false
    )
    private UsuarioEmpresaModel usuarioEmpresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_subsidiaria",
            nullable = false
    )
    private SubsidiariaModel subsidiaria;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public UsuarioSubsidiariaModel(
            UsuarioEmpresaModel usuarioEmpresa,
            SubsidiariaModel subsidiaria
    ) {
        this.usuarioEmpresa = usuarioEmpresa;
        this.subsidiaria = subsidiaria;
        this.status = StatusEnum.ATIVO;
    }

    public void inativar() {
        this.status = StatusEnum.INATIVO;
    }

    public void remover(Long idUsuario) {
        this.status = StatusEnum.REMOVIDO;
        registrarRemocao(idUsuario);
    }
}