package com.empresa.erp.domain.usuario.criacao.service;

import java.util.Comparator;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.empresa.erp.core.exception.ValidacaoException;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriacaoUsuarioService {

    private final UsuarioRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final Validator validator;

    @Transactional
    public UsuarioModel criar(
            String email,
            String senha
    ) {
        String emailNormalizado =
                normalizarEmail(email);

        validarDadosObrigatorios(
                emailNormalizado,
                senha
        );

        UsuarioRecord dados =
                new UsuarioRecord(
                        emailNormalizado,
                        senha
                );

        validarDados(dados);

        if (repository.existsByEmailIgnoreCase(
                emailNormalizado
        )) {
            throw new ValidacaoException(
                    "Usuario ja cadastrado."
            );
        }

        UsuarioModel usuario =
                new UsuarioModel(
                        dados,
                        passwordEncoder.encode(senha)
                );

        return repository.save(usuario);
    }

    private void validarDadosObrigatorios(
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

    private void validarDados(
            UsuarioRecord dados
    ) {
        Set<ConstraintViolation<UsuarioRecord>>
                violacoes =
                validator.validate(dados);

        if (violacoes.isEmpty()) {
            return;
        }

        ConstraintViolation<UsuarioRecord> violacao =
                violacoes.stream()
                        .sorted(
                                Comparator.comparing(
                                        item ->
                                                item.getPropertyPath()
                                                        .toString()
                                )
                        )
                        .findFirst()
                        .orElseThrow();

        throw new ValidacaoException(
                violacao.getMessage()
        );
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