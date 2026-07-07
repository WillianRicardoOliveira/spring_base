package com.empresa.erp.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.record.SsoLoginSecurity;
import com.empresa.erp.core.security.record.TokenJwtSecurity;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager manager;

    private final TokenSecurity tokenService;

    private final SsoSecurity ssoSecurity;

    @PostMapping
    public ResponseEntity<TokenJwtSecurity> efetuarLogin(@RequestBody @Valid UsuarioRecord dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        UsuarioAutenticado usuarioAutenticado = (UsuarioAutenticado) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuarioAutenticado.getUsuario());

        return ResponseEntity.ok(new TokenJwtSecurity(tokenJWT));
    }

    @PostMapping("/sso")
    public ResponseEntity<TokenJwtSecurity> efetuarLoginSso(@RequestBody @Valid SsoLoginSecurity dados) {
        return ResponseEntity.ok(ssoSecurity.autenticar(dados));
    }

}