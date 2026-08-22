package com.empresa.erp.domain.plataforma.organizacao.convite.record;

public record ConsultaConviteOrganizacaoRecord(
        String nomeOrganizacao,
        String emailAdministradorMascarado,
        boolean usuarioExistente
) {
}