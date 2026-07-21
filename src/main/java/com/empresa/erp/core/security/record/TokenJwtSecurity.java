package com.empresa.erp.core.security.record;

public record TokenJwtSecurity(
        String token,
        String refreshToken
) {

    public TokenJwtSecurity(String token) {
        this(token, null);
    }

    @Override
    public String toString() {
        return "TokenJwtSecurity[token=****, refreshToken=****]";
    }
}