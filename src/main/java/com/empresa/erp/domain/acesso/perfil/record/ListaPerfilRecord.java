package com.empresa.erp.domain.acesso.perfil.record;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.old.StatusEnum;

public record ListaPerfilRecord(
    Long id,
    String nome,
    String descricao,
    StatusEnum status
) {

    public ListaPerfilRecord(PerfilModel dados) {
        this(
            dados.getId(),
            dados.getNome(),
            dados.getDescricao(),
            dados.getStatus()
        );
    }
}