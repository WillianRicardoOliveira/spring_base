package com.empresa.erp.core.security.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.service.UsuarioLoginTentativaService;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;

class AutenticacaoControllerPermissoesTest {

    private AuthenticationManager manager;

    private TokenSecurity tokenService;

    private SsoSecurity ssoSecurity;

    private UsuarioSessaoService
            usuarioSessaoService;

    private UsuarioLoginTentativaService
            usuarioLoginTentativaService;

    private AutenticacaoController controller;

    @BeforeEach
    void setUp() {
        manager =
                mock(
                        AuthenticationManager.class
                );

        tokenService =
                mock(
                        TokenSecurity.class
                );

        ssoSecurity =
                mock(
                        SsoSecurity.class
                );

        usuarioSessaoService =
                mock(
                        UsuarioSessaoService.class
                );

        usuarioLoginTentativaService =
                mock(
                        UsuarioLoginTentativaService.class
                );

        controller =
                new AutenticacaoController(
                        manager,
                        tokenService,
                        ssoSecurity,
                        usuarioSessaoService,
                        usuarioLoginTentativaService
                );
    }

    @Test
    @DisplayName(
            "Deve retornar as permissoes do usuario autenticado"
    )
    void deveRetornarAsPermissoesDoUsuarioAutenticado() {
        var usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com"
                );
        var usuarioAutenticado =
                new UsuarioAutenticado(
                        usuario,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ACESSO_USUARIO_LISTAR"
                                ),
                                new SimpleGrantedAuthority(
                                        "ACESSO_PERFIL_DETALHAR"
                                )
                        )
                );

        var authentication =
                new UsernamePasswordAuthenticationToken(
                        usuarioAutenticado,
                        null,
                        usuarioAutenticado.getAuthorities()
                );

        var resposta =
                controller.consultarPermissoes(
                        authentication
                );

        assertEquals(
                HttpStatus.OK,
                resposta.getStatusCode()
        );

        var corpo =
                resposta.getBody();

        assertNotNull(corpo);

        assertEquals(
                List.of(
                        "ACESSO_PERFIL_DETALHAR",
                        "ACESSO_USUARIO_LISTAR"
                ),
                corpo.permissoes()
        );

        verifyNoInteractions(
                manager,
                tokenService,
                ssoSecurity,
                usuarioSessaoService,
                usuarioLoginTentativaService
        );
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email
    ) {
        var usuario =
                new UsuarioModel(
                        new UsuarioRecord(
                                email,
                                "Senha@123"
                        ),
                        "senha-criptografada"
                );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                id
        );

        return usuario;
    }
}