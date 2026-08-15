package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import jakarta.validation.constraints.NotNull;

public record AtualizaUsuarioEmpresaRecord(

        @NotNull(
                message = "{usuario.empresa.id.obrigatorio}"
        )
        Long id,

        @NotNull(
                message = "{usuario.empresa.todas_subsidiarias.obrigatorio}"
        )
        Boolean todasSubsidiarias

) {
}