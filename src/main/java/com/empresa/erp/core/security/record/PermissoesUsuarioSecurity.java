package com.empresa.erp.core.security.record;

import java.util.List;

public record PermissoesUsuarioSecurity(
        List<String> permissoes
) {
}