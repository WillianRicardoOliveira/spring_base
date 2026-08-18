package com.empresa.erp.domain.organizacao.record;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;

public record OrganizacaoDisponivelRecord(
        Long id,
        String nome
) {

    public OrganizacaoDisponivelRecord(
            UsuarioOrganizacaoModel vinculo
    ) {
        this(
                vinculo.getOrganizacao().getId(),
                vinculo.getOrganizacao().getNome()
        );
    }
}