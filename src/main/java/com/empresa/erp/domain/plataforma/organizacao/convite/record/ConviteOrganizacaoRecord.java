package com.empresa.erp.domain.plataforma.organizacao.convite.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConviteOrganizacaoRecord(

        @NotBlank(
                message = "{organizacao.nome.obrigatorio}"
        )
        @Size(
                max = 100,
                message = "{organizacao.nome.tamanho}"
        )
        String nomeOrganizacao,

        @NotBlank(
                message = "{convite.organizacao.email.obrigatorio}"
        )
        @Email(
                message = "{convite.organizacao.email.invalido}"
        )
        @Size(
                max = 100,
                message = "{convite.organizacao.email.tamanho}"
        )
        String emailAdministrador

) {
}