package com.empresa.erp.domain.acesso.usuarioSessao.scheduler;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;

class UsuarioSessaoSchedulerTest {

    @Test
    @DisplayName("Deve revogar sessoes expiradas")
    void deveRevogarSessoesExpiradas() {
        var usuarioSessaoService = org.mockito.Mockito.mock(UsuarioSessaoService.class);
        var scheduler = new UsuarioSessaoScheduler(usuarioSessaoService);

        scheduler.revogarSessoesExpiradas();

        verify(usuarioSessaoService).revogarSessoesExpiradas();
    }
}