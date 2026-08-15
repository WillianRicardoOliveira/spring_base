package com.empresa.erp.domain.acesso.usuarioSubsidiaria.record;

import jakarta.validation.constraints.NotNull;

public record UsuarioSubsidiariaRecord(

        @NotNull(
                message = "{usuario.subsidiaria.usuario_empresa.obrigatorio}"
        )
        Long idUsuarioEmpresa,

        @NotNull(
                message = "{usuario.subsidiaria.subsidiaria.obrigatoria}"
        )
        Long idSubsidiaria

) {
}