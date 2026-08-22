package com.empresa.erp.domain.usuario.criacao.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriacaoUsuarioService {

    private final UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioModel criar(
            String email,
            String senha
    ) {
        String emailNormalizado =
                normalizarEmail(email);

        validarDados(
                emailNormalizado,
                senha
        );

        if (repository.existsByEmailIgnoreCase(
                emailNormalizado
        )) {
            throw new ValidacaoException(
                    "Usuario ja cadastrado."
            );
        }

        UsuarioRecord dados =
                new UsuarioRecord(
                        emailNormalizado,
                        senha
                );

        UsuarioModel usuario =
                new UsuarioModel(
                        dados,
                        passwordEncoder.encode(senha)
                );

        return repository.save(usuario);
    }

    private void validarDados(
            String email,
            String senha
    ) {
        if (email == null || email.isBlank()) {
            throw new ValidacaoException(
                    "E-mail do usuario obrigatorio."
            );
        }

        if (senha == null || senha.isBlank()) {
            throw new ValidacaoException(
                    "Senha do usuario obrigatoria."
            );
        }
    }

    private String normalizarEmail(
            String email
    ) {
        return email == null
                ? null
                : email
                        .trim()
                        .toLowerCase(Locale.ROOT);
    }
}