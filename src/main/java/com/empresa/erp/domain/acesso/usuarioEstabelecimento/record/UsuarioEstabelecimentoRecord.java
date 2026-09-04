package com.empresa.erp.domain.acesso.usuarioEstabelecimento.record;

import jakarta.validation.constraints.NotNull;

public record UsuarioEstabelecimentoRecord(

        @NotNull(message = "{usuario.estabelecimento.usuario_empresa.obrigatorio}")
        Long idUsuarioEmpresa,

        @NotNull(message = "{usuario.estabelecimento.estabelecimento.obrigatorio}")
        Long idEstabelecimento

) {
}