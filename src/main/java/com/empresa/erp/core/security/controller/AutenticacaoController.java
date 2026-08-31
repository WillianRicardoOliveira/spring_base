package com.empresa.erp.core.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.record.LoginSecurity;
import com.empresa.erp.core.security.record.PermissoesUsuarioSecurity;
import com.empresa.erp.core.security.record.RefreshTokenSecurity;
import com.empresa.erp.core.security.record.SsoLoginSecurity;
import com.empresa.erp.core.security.record.TokenJwtSecurity;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.service.UsuarioLoginTentativaService;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager manager;

    private final TokenSecurity tokenService;

    private final SsoSecurity ssoSecurity;

    private final UsuarioSessaoService
            usuarioSessaoService;

    private final UsuarioLoginTentativaService
            usuarioLoginTentativaService;

    @PostMapping
    public ResponseEntity<TokenJwtSecurity>
            efetuarLogin(
                    @RequestBody
                    @Valid
                    LoginSecurity dados,
                    HttpServletRequest request,
                    @RequestHeader(
                            value = "User-Agent",
                            required = false
                    )
                    String userAgent
            ) {
        usuarioLoginTentativaService
                .validarLoginPermitido(
                        dados.email()
                );

        try {
            var authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            dados.email(),
                            dados.senha()
                    );

            var authentication =
                    manager.authenticate(
                            authenticationToken
                    );

            usuarioLoginTentativaService
                    .registrarSucesso(
                            dados.email()
                    );

            UsuarioAutenticado
                    usuarioAutenticado =
                    (UsuarioAutenticado)
                            authentication
                                    .getPrincipal();

            var usuario =
                    usuarioAutenticado
                            .getUsuario();

            var tokenGerado =
                    tokenService
                            .gerarTokenComJti(
                                    usuario
                            );

            var refreshToken =
                    usuarioSessaoService
                            .criarSessao(
                                    usuario,
                                    tokenGerado.jti(),
                                    recuperarIp(request),
                                    userAgent
                            );

            return ResponseEntity.ok(
                    new TokenJwtSecurity(
                            tokenGerado.token(),
                            refreshToken
                    )
            );
        } catch (RuntimeException exception) {
            usuarioLoginTentativaService
                    .registrarFalha(
                            dados.email()
                    );

            throw exception;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenJwtSecurity>
            renovarToken(
                    @RequestBody
                    @Valid
                    RefreshTokenSecurity dados
            ) {
        return ResponseEntity.ok(
                usuarioSessaoService
                        .renovarSessao(
                                dados.refreshToken()
                        )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody
            @Valid
            RefreshTokenSecurity dados
    ) {
        usuarioSessaoService
                .revogarSessao(
                        dados.refreshToken()
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/sso")
    public ResponseEntity<TokenJwtSecurity>
            efetuarLoginSso(
                    @RequestBody
                    @Valid
                    SsoLoginSecurity dados,
                    HttpServletRequest request,
                    @RequestHeader(
                            value = "User-Agent",
                            required = false
                    )
                    String userAgent
            ) {
        var usuario =
                ssoSecurity
                        .autenticar(dados);

        var tokenGerado =
                tokenService
                        .gerarTokenComJti(
                                usuario
                        );

        var refreshToken =
                usuarioSessaoService
                        .criarSessao(
                                usuario,
                                tokenGerado.jti(),
                                recuperarIp(request),
                                userAgent
                        );

        return ResponseEntity.ok(
                new TokenJwtSecurity(
                        tokenGerado.token(),
                        refreshToken
                )
        );
    }

    @GetMapping("/permissoes")
    public ResponseEntity<PermissoesUsuarioSecurity> consultarPermissoes(
            Authentication authentication
    ) {
        var permissoes = authentication
                .getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .sorted()
                .toList();

        return ResponseEntity.ok(
                new PermissoesUsuarioSecurity(permissoes)
        );
    }

    private String recuperarIp(
            HttpServletRequest request
    ) {
        String forwardedFor =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (StringUtils.hasText(forwardedFor)) {
            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        return request.getRemoteAddr();
    }
}