package com.empresa.erp.controller.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.record.SsoLoginSecurity;
import com.empresa.erp.core.security.record.TokenJwtSecurity;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

class AutenticacaoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private AuthenticationManager manager;
    private TokenSecurity tokenService;
    private SsoSecurity ssoSecurity;

    @BeforeEach
    void setUp() {
        manager = org.mockito.Mockito.mock(AuthenticationManager.class);
        tokenService = org.mockito.Mockito.mock(TokenSecurity.class);
        ssoSecurity = org.mockito.Mockito.mock(SsoSecurity.class);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AutenticacaoController(manager, tokenService, ssoSecurity))
                .build();
    }

    @Test
    @DisplayName("Deve efetuar login e retornar token JWT")
    void deveEfetuarLoginERetornarTokenJwt() throws Exception {
        var dados = new UsuarioRecord("usuario@teste.com", "123456");
        var usuario = criarUsuario(1L, "usuario@teste.com");
        var usuarioAutenticado = new UsuarioAutenticado(usuario, List.of());
        var authentication = new UsernamePasswordAuthenticationToken(usuarioAutenticado, dados.senha(), List.of());

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenService.gerarToken(usuario)).thenReturn("jwt-token");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).gerarToken(usuario);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login com email em branco")
    void deveRetornar400AoEfetuarLoginComEmailEmBranco() throws Exception {
        var dados = new UsuarioRecord("", "123456");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login com email invalido")
    void deveRetornar400AoEfetuarLoginComEmailInvalido() throws Exception {
        var dados = new UsuarioRecord("email-invalido", "123456");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity);
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login com senha em branco")
    void deveRetornar400AoEfetuarLoginComSenhaEmBranco() throws Exception {
        var dados = new UsuarioRecord("usuario@teste.com", "");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(manager, tokenService, ssoSecurity);
    }

    @Test
    @DisplayName("Deve efetuar login SSO e retornar token JWT")
    void deveEfetuarLoginSsoERetornarTokenJwt() throws Exception {
        var dados = new SsoLoginSecurity("sso-token");

        when(ssoSecurity.autenticar(any(SsoLoginSecurity.class)))
                .thenReturn(new TokenJwtSecurity("jwt-sso-token"));

        mockMvc.perform(post("/login/sso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-sso-token"));

        verify(ssoSecurity).autenticar(any(SsoLoginSecurity.class));
    }

    @Test
    @DisplayName("Deve retornar 400 ao efetuar login SSO sem token")
    void deveRetornar400AoEfetuarLoginSsoSemToken() throws Exception {
        var dados = new SsoLoginSecurity("");

        mockMvc.perform(post("/login/sso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ssoSecurity);
    }

    private UsuarioModel criarUsuario(Long id, String email) {
        var usuario = new UsuarioModel(new UsuarioRecord(email, "123456"), "senha-criptografada");
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}