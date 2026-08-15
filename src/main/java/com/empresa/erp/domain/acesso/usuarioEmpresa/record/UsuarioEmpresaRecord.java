package com.empresa.erp.domain.acesso.usuarioEmpresa.record;

import jakarta.validation.constraints.NotNull;

public record UsuarioEmpresaRecord(

        @NotNull(
                message = "{usuario.empresa.usuario.obrigatorio}"
        )
        Long idUsuario,

        @NotNull(
                message = "{usuario.empresa.empresa.obrigatoria}"
        )
        Long idEmpresa,

        @NotNull(
                message = "{usuario.empresa.todas_subsidiarias.obrigatorio}"
        )
        Boolean todasSubsidiarias

) {
}