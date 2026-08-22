package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import jakarta.validation.constraints.NotBlank;

public record AceiteConviteOrganizacaoUsuarioExistenteRecord(

        @NotBlank(
                message = "{convite.organizacao.token.obrigatorio}"
        )
        String token

) {
}