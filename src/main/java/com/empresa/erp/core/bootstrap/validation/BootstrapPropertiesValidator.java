package com.empresa.erp.core.bootstrap.validation;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.empresa.erp.core.bootstrap.config.BootstrapProperties;
import com.empresa.erp.core.validation.SenhaForte;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BootstrapPropertiesValidator {

    private static final int TAMANHO_MAXIMO_NOME_ORGANIZACAO =
            100;

    private static final int TAMANHO_MAXIMO_EMAIL =
            100;

    private static final int TAMANHO_MAXIMO_SENHA_BCRYPT_BYTES =
            72;

    private static final Set<String> SENHAS_PADRAO = Set.of(
            "admin@123",
            "admin123!",
            "password@123",
            "senha@123"
    );

    private final Validator validator;

    public void validar(
            BootstrapProperties properties
    ) {
        if (!properties.enabled()) {
            return;
        }

        validarNomeOrganizacao(
                properties.organizationName()
        );

        validarCredenciais(
                "administrador da organizacao",
                properties.organizationAdminEmail(),
                properties.organizationAdminPassword()
        );

        validarCredenciais(
                "administrador da plataforma",
                properties.platformAdminEmail(),
                properties.platformAdminPassword()
        );

        validarIdentidadeCompartilhada(properties);
    }

    private void validarNomeOrganizacao(
            String nome
    ) {
        if (!StringUtils.hasText(nome)) {
            throw configuracaoInvalida(
                    "nome da organizacao obrigatorio"
            );
        }

        if (!nome.equals(nome.trim())) {
            throw configuracaoInvalida(
                    "nome da organizacao nao deve iniciar "
                            + "ou terminar com espacos"
            );
        }

        if (nome.length()
                > TAMANHO_MAXIMO_NOME_ORGANIZACAO) {
            throw configuracaoInvalida(
                    "nome da organizacao deve possuir "
                            + "no maximo 100 caracteres"
            );
        }
    }

    private void validarCredenciais(
            String identificacao,
            String email,
            String senha
    ) {
        if (email != null) {
            validarEmail(
                    identificacao,
                    email
            );
        }

        var credenciais =
                new CredenciaisAdministrador(
                        email,
                        senha
                );

        Set<ConstraintViolation<CredenciaisAdministrador>>
                violacoes =
                validator.validate(credenciais);

        if (!violacoes.isEmpty()) {
            throw configuracaoInvalida(
                    "e-mail ou senha do "
                            + identificacao
                            + " invalidos"
            );
        }

        validarSenha(
                identificacao,
                senha
        );
    }

    private void validarEmail(
            String identificacao,
            String email
    ) {
        if (!email.equals(email.trim())) {
            throw configuracaoInvalida(
                    "e-mail do "
                            + identificacao
                            + " nao deve iniciar ou terminar "
                            + "com espacos"
            );
        }

        if (email.length() > TAMANHO_MAXIMO_EMAIL) {
            throw configuracaoInvalida(
                    "e-mail do "
                            + identificacao
                            + " deve possuir no maximo "
                            + "100 caracteres"
            );
        }
    }

    private void validarSenha(
            String identificacao,
            String senha
    ) {
        int tamanhoEmBytes =
                senha.getBytes(
                        StandardCharsets.UTF_8
                ).length;

        if (tamanhoEmBytes
                > TAMANHO_MAXIMO_SENHA_BCRYPT_BYTES) {
            throw configuracaoInvalida(
                    "senha do "
                            + identificacao
                            + " excede o limite seguro "
                            + "do BCrypt"
            );
        }

        String senhaNormalizada =
                senha.toLowerCase(Locale.ROOT);

        if (SENHAS_PADRAO.contains(
                senhaNormalizada
        )) {
            throw configuracaoInvalida(
                    "senha padrao nao permitida para o "
                            + identificacao
            );
        }
    }

    private void validarIdentidadeCompartilhada(
            BootstrapProperties properties
    ) {
        String emailOrganizacao =
                normalizarEmail(
                        properties.organizationAdminEmail()
                );

        String emailPlataforma =
                normalizarEmail(
                        properties.platformAdminEmail()
                );

        if (!emailOrganizacao.equals(emailPlataforma)) {
            return;
        }

        if (!Objects.equals(
                properties.organizationAdminPassword(),
                properties.platformAdminPassword()
        )) {
            throw configuracaoInvalida(
                    "as senhas devem ser iguais quando "
                            + "o administrador da organizacao "
                            + "e o administrador da plataforma "
                            + "utilizam o mesmo e-mail"
            );
        }
    }

    private String normalizarEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private IllegalStateException configuracaoInvalida(
            String motivo
    ) {
        return new IllegalStateException(
                "Configuracao de bootstrap invalida: "
                        + motivo
        );
    }

    private record CredenciaisAdministrador(
            @NotBlank
            @Email
            String email,

            @NotBlank
            @SenhaForte
            String senha
    ) {
    }
}