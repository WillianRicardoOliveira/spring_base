package com.empresa.erp.domain.usuario.record;

import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.padrao.constant.StatusEnum;

public record DetalheUsuarioRecord(
    Long id,
    String email,
    StatusEnum status
) {
    public DetalheUsuarioRecord(UsuarioModel dados) {
        this(
        		dados.getId(),
        		dados.getEmail(),
        		dados.getStatus()
        	);
    }
}