package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import com.empresa.erp.core.validation.SenhaForte;

import jakarta.validation.constraints.NotBlank;

public record AceiteConviteOrganizacaoNovoUsuarioRecord(

        @NotBlank(
                message = "{convite.organizacao.token.obrigatorio}"
        )
        String token,

        @NotBlank(
                message = "{usuario.senha.obrigatorio}"
        )
        @SenhaForte
        String senha

) {
}