package com.empresa.erp.core.security.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class TokenOpacoService {

    private static final int TAMANHO_TOKEN_BYTES =
            32;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public String gerar() {
        byte[] bytes =
                new byte[TAMANHO_TOKEN_BYTES];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public String gerarHash(
            String token
    ) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token obrigatorio."
            );
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Algoritmo SHA-256 indisponivel.",
                    exception
            );
        }
    }
}