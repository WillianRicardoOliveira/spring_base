package com.empresa.erp.domain.base.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditoriaModel {

    @CreatedDate
    private LocalDateTime criadoEm;

    @CreatedBy
    private Long criadoPor;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    @LastModifiedBy
    private Long atualizadoPor;

    private LocalDateTime removidoEm;

    private Long removidoPor;

    protected void registrarRemocao(Long idUsuario) {
        this.removidoEm = LocalDateTime.now();
        this.removidoPor = idUsuario;
    }
}