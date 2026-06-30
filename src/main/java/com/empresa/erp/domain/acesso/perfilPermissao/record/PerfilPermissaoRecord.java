package com.empresa.erp.domain.acesso.perfilPermissao.record;

import jakarta.validation.constraints.NotNull;

public record PerfilPermissaoRecord(
    @NotNull(message = "{perfil.permissao.perfil.obrigatorio}")
    Long idPerfil,
    @NotNull(message = "{perfil.permissao.permissao.obrigatoria}")
    Long idPermissao
) {}