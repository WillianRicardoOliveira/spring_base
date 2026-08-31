package com.empresa.erp.domain.plataforma.organizacao.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrganizacaoRecord(

        @NotBlank(
                message = "{organizacao.nome.obrigatorio}"
        )
        @Size(
                max = 100,
                message = "{organizacao.nome.tamanho}"
        )
        String nome

) {
}