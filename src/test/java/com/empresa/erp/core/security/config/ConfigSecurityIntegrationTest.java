package com.empresa.erp.core.security.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.empresa.erp.controller.auth.AutenticacaoController;
import com.empresa.erp.core.security.filter.FilterSecurity;
import com.empresa.erp.core.security.handler.AcessoNegadoHandler;
import com.empresa.erp.core.security.handler.AutenticacaoEntryPoint;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.service.UsuarioLoginTentativaService;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;

@WebMvcTest(
        controllers = AutenticacaoController.class,
        properties = "app.security.swagger-public=false"
)
@Import({
        ConfigSecurity.class,
        FilterSecurity.class,
        AutenticacaoEntryPoint.class,
        AcessoNegadoHandler.class
})
class ConfigSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager manager;

    @MockitoBean
    private TokenSecurity tokenSecurity;

    @MockitoBean
    private SsoSecurity ssoSecurity;

    @MockitoBean
    private UsuarioSessaoService usuarioSessaoService;

    @MockitoBean
    private UsuarioLoginTentativaService usuarioLoginTentativaService;

    @MockitoBean
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Test
    @DisplayName("Deve permitir acesso publico ao login")
    void devePermitirAcessoPublicoAoLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve permitir acesso publico ao refresh token")
    void devePermitirAcessoPublicoAoRefreshToken() throws Exception {
        mockMvc.perform(post("/login/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve permitir acesso publico ao logout")
    void devePermitirAcessoPublicoAoLogout() throws Exception {
        mockMvc.perform(post("/login/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve permitir acesso publico ao login SSO")
    void devePermitirAcessoPublicoAoLoginSso() throws Exception {
        mockMvc.perform(post("/login/sso")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve bloquear endpoint protegido sem autenticacao")
    void deveBloquearEndpointProtegidoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/perfil"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deve bloquear Swagger quando swagger-public estiver false")
    void deveBloquearSwaggerQuandoNaoPublico() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}