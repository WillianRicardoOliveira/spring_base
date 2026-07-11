package com.empresa.erp.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    private static final int TAMANHO_MINIMO = 8;

    @Override
    public boolean isValid(String senha, ConstraintValidatorContext context) {
        if (senha == null || senha.isBlank()) {
            return true;
        }

        return senha.length() >= TAMANHO_MINIMO
                && possuiLetraMinuscula(senha)
                && possuiLetraMaiuscula(senha)
                && possuiNumero(senha)
                && possuiCaractereEspecial(senha);
    }

    private boolean possuiLetraMinuscula(String senha) {
        return senha.chars().anyMatch(Character::isLowerCase);
    }

    private boolean possuiLetraMaiuscula(String senha) {
        return senha.chars().anyMatch(Character::isUpperCase);
    }

    private boolean possuiNumero(String senha) {
        return senha.chars().anyMatch(Character::isDigit);
    }

    private boolean possuiCaractereEspecial(String senha) {
        return senha.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
    }
}