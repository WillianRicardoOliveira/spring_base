package com.empresa.erp.domain.acesso.usuarioSessao.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsuarioSessaoScheduler {

    private final UsuarioSessaoService usuarioSessaoService;

    @Scheduled(cron = "${api.security.refresh-token.cleanup-cron}")
    public void revogarSessoesExpiradas() {
        usuarioSessaoService.revogarSessoesExpiradas();
    }
}