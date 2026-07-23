package com.empresa.erp.domain.acesso.usuarioLoginTentativa.model;

import java.time.LocalDateTime;

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

@Table(name = "usuario_login_tentativa")
@Entity(name = "UsuarioLoginTentativaModel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class UsuarioLoginTentativaModel extends AuditoriaModel {

    private static final int ZERO_FALHAS = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private Integer quantidadeFalhas;

    private LocalDateTime ultimaFalhaEm;

    private LocalDateTime bloqueadoAte;

    @Enumerated(EnumType.ORDINAL)
    private StatusEnum status;

    public UsuarioLoginTentativaModel(String email) {
        this.email = normalizarEmail(email);
        this.quantidadeFalhas = ZERO_FALHAS;
        this.status = StatusEnum.ATIVO;
    }

    public void registrarFalha(int limiteFalhas, int minutosBloqueio) {
        this.quantidadeFalhas++;
        this.ultimaFalhaEm = LocalDateTime.now();

        if (this.quantidadeFalhas >= limiteFalhas) {
            this.bloqueadoAte = LocalDateTime.now().plusMinutes(minutosBloqueio);
        }
    }

    public void limparFalhas() {
        this.quantidadeFalhas = ZERO_FALHAS;
        this.ultimaFalhaEm = null;
        this.bloqueadoAte = null;
    }

    public boolean estaBloqueado() {
        return this.bloqueadoAte != null && this.bloqueadoAte.isAfter(LocalDateTime.now());
    }

    public void remover(Long idUsuario) {
        this.status = StatusEnum.REMOVIDO;
        registrarRemocao(idUsuario);
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}