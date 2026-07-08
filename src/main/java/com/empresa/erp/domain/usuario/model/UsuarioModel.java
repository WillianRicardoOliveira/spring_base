package com.empresa.erp.domain.usuario.model;

import com.empresa.erp.domain.base.model.AuditoriaModel;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.record.AtualizaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

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

@Table(name = "usuario")
@Entity(name = "UsuarioModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsuarioModel extends AuditoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String senha;
    
    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public UsuarioModel(UsuarioRecord dados, String senhaCriptografada) {
        this.email = normalizarEmail(dados.email());
        this.senha = senhaCriptografada;
        this.status = StatusEnum.ATIVO;
    }

    public void atualizar(AtualizaUsuarioRecord dados) {
        this.email = normalizarEmail(dados.email());
    }

    public void atualizarSenha(String senhaCriptografada) {
        this.senha = senhaCriptografada;
    }

    public void inativar() {
    	this.status = StatusEnum.INATIVO;
    }
    
    public void remover(Long idUsuario) {
        this.status = StatusEnum.REMOVIDO;
        registrarRemocao(idUsuario);
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public boolean isEnabled() {
        return StatusEnum.ATIVO.equals(this.status);
    }
    
}