package com.empresa.erp.domain.acesso.usuarioPerfil.record;

import jakarta.validation.constraints.NotNull;

public record UsuarioPerfilRecord(
    @NotNull(message = "{usuario.perfil.usuario.obrigatorio}")
    Long idUsuario,
    @NotNull(message = "{usuario.perfil.perfil.obrigatorio}")
    Long idPerfil
) {}