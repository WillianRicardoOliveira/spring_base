package com.empresa.erp.domain.usuario.record;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

public record ListaUsuarioRecord(
    Long id,
    String email,
    StatusEnum status
) {
    public ListaUsuarioRecord(UsuarioModel dados) {
        this(
        		dados.getId(),
        		dados.getEmail(),
        		dados.getStatus()
        	);
    }
}