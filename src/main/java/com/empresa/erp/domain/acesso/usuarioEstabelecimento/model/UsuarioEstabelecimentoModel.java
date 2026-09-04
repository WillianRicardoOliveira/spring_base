package com.empresa.erp.domain.acesso.usuarioEstabelecimento.model;

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;

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

@Table(name = "usuario_estabelecimento")
@Entity(name = "UsuarioEstabelecimentoModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsuarioEstabelecimentoModel extends AuditoriaModel {

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
            name = "id_estabelecimento",
            nullable = false
    )
    private EstabelecimentoModel estabelecimento;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private StatusEnum status;

    public UsuarioEstabelecimentoModel(
            UsuarioEmpresaModel usuarioEmpresa,
            EstabelecimentoModel estabelecimento
    ) {
        this.usuarioEmpresa = usuarioEmpresa;
        this.estabelecimento = estabelecimento;
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