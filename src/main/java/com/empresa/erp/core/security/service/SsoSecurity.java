package com.empresa.erp.core.security.service;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.empresa.erp.core.exception.SsoAuthenticationException;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.record.SsoLoginSecurity;
import com.empresa.erp.core.security.record.TokenJwtSecurity;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.repository.UsuarioRepository;

@Service
public class SsoSecurity {

    private final ObjectProvider<JwtDecoder> jwtDecoderProvider;
    private final UsuarioRepository usuarioRepository;
    private final TokenSecurity tokenSecurity;
    private final String emailClaim;
    private final String audience;
    private final String scope;

    public SsoSecurity(
            ObjectProvider<JwtDecoder> jwtDecoderProvider,
            UsuarioRepository usuarioRepository,
            TokenSecurity tokenSecurity,
            @Value("${sso.claim.email:email}") String emailClaim,
            @Value("${sso.audience:}") String audience,
            @Value("${sso.scope:}") String scope) {
        this.jwtDecoderProvider = jwtDecoderProvider;
        this.usuarioRepository = usuarioRepository;
        this.tokenSecurity = tokenSecurity;
        this.emailClaim = emailClaim;
        this.audience = audience;        
        this.scope = scope;
    }

    public TokenJwtSecurity autenticar(SsoLoginSecurity dados) {
    	
        if (dados == null || !StringUtils.hasText(dados.token())) {
            throw new SsoAuthenticationException("Token SSO nao informado");
        }

        var jwtDecoder = jwtDecoderProvider.getIfAvailable();

        if (jwtDecoder == null) {
            throw new SsoAuthenticationException("SSO nao configurado");
        }

        try {
            Jwt jwt = jwtDecoder.decode(dados.token());
            validarAudience(jwt);
            validarScope(jwt);
            String email = extrairEmail(jwt);
            UsuarioModel usuario = usuarioRepository.findByEmailIgnoreCase(email);

            if (usuario == null || !usuario.isEnabled()) {
                throw new SsoAuthenticationException("Usuario nao autorizado para SSO");
            }

            return new TokenJwtSecurity(tokenSecurity.gerarToken(usuario));
        } catch (JwtException e) {
            throw new SsoAuthenticationException("Token SSO invalido ou expirado", e);
        }
    }

    private String extrairEmail(Jwt jwt) {
        String email = claimComoTexto(jwt, emailClaim);

        if (!StringUtils.hasText(email)) {
            email = claimComoTexto(jwt, "email");
        }

        if (!StringUtils.hasText(email)) {
            email = claimComoTexto(jwt, "preferred_username");
        }

        if (!StringUtils.hasText(email)) {
            email = claimComoTexto(jwt, "upn");
        }

        if (!StringUtils.hasText(email)) {
            throw new SsoAuthenticationException("E-mail nao encontrado no token SSO");
        }

        return email.trim().toLowerCase();
    }

    private String claimComoTexto(Jwt jwt, String claim) {
        Object valor = jwt.getClaims().get(claim);
        return valor instanceof String texto ? texto : null;
    }
    
    private void validarAudience(Jwt jwt) {
        if (StringUtils.hasText(audience) && !jwt.getAudience().contains(audience)) {
            throw new SsoAuthenticationException("Token SSO nao pertence a esta aplicacao");
        }
    }
    
    private void validarScope(Jwt jwt) {
        if (!StringUtils.hasText(scope)) {
            return;
        }

        String scopes = claimComoTexto(jwt, "scp");

        if (!StringUtils.hasText(scopes)) {
            throw new SsoAuthenticationException("Token SSO sem escopo autorizado");
        }

        boolean autorizado = List.of(scopes.trim().split("\\s+")).contains(scope);

        if (!autorizado) {
            throw new SsoAuthenticationException("Token SSO sem permissao para login");
        }
    }
    
}