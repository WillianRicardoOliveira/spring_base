package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import jakarta.validation.constraints.NotBlank;

public record TokenConsultaConviteOrganizacaoRecord(

        @NotBlank(
                message = "{convite.organizacao.token.obrigatorio}"
        )
        String token

) {
}