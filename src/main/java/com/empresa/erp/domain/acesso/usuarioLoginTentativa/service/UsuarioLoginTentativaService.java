package com.empresa.erp.domain.acesso.usuarioLoginTentativa.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.model.UsuarioLoginTentativaModel;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.repository.UsuarioLoginTentativaRepository;
import com.empresa.erp.domain.old.StatusEnum;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioLoginTentativaService {

    private static final int LIMITE_MINIMO_FALHAS = 3;
    private static final int MINUTOS_MINIMO_BLOQUEIO = 1;

    private final UsuarioLoginTentativaRepository repository;

    @Value("${app.security.login.max-failed-attempts}")
    private Integer maxFailedAttempts;

    @Value("${app.security.login.lock-minutes}")
    private Integer lockMinutes;

    @PostConstruct
    void validarConfiguracao() {
        if (maxFailedAttempts == null || maxFailedAttempts < LIMITE_MINIMO_FALHAS) {
            throw new IllegalStateException(
                    "app.security.login.max-failed-attempts deve ser configurado com no minimo 3"
            );
        }

        if (lockMinutes == null || lockMinutes < MINUTOS_MINIMO_BLOQUEIO) {
            throw new IllegalStateException(
                    "app.security.login.lock-minutes deve ser configurado com no minimo 1"
            );
        }
    }

    @Transactional(readOnly = true)
    public void validarLoginPermitido(String email) {
        if (!StringUtils.hasText(email)) {
            return;
        }

        repository.findByEmailIgnoreCaseAndStatus(normalizarEmail(email), StatusEnum.ATIVO)
                .filter(UsuarioLoginTentativaModel::estaBloqueado)
                .ifPresent(tentativa -> {
                    throw new ValidacaoException("Login temporariamente bloqueado. Tente novamente mais tarde.");
                });
    }

    @Transactional
    public void registrarFalha(String email) {
        if (!StringUtils.hasText(email)) {
            return;
        }

        var tentativa = repository.findByEmailIgnoreCaseAndStatus(normalizarEmail(email), StatusEnum.ATIVO)
                .orElseGet(() -> new UsuarioLoginTentativaModel(email));

        tentativa.registrarFalha(maxFailedAttempts, lockMinutes);

        repository.save(tentativa);
    }

    @Transactional
    public void registrarSucesso(String email) {
        if (!StringUtils.hasText(email)) {
            return;
        }

        repository.findByEmailIgnoreCaseAndStatus(normalizarEmail(email), StatusEnum.ATIVO)
                .ifPresent(tentativa -> {
                    tentativa.limparFalhas();
                    repository.save(tentativa);
                });
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}