package com.empresa.erp.core.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.empresa.erp.core.organizacao.filter.ContextoOrganizacaoFilter;
import com.empresa.erp.core.organizacao.service.ContextoOrganizacaoService;
import com.empresa.erp.core.security.controller.AutenticacaoController;
import com.empresa.erp.core.security.filter.AutoridadesPlataformaFilter;
import com.empresa.erp.core.security.filter.FilterSecurity;
import com.empresa.erp.core.security.handler.AcessoNegadoHandler;
import com.empresa.erp.core.security.handler.AutenticacaoEntryPoint;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.service.AutoridadesOrganizacaoService;
import com.empresa.erp.core.security.service.AutoridadesPlataformaService;
import com.empresa.erp.core.security.service.SsoSecurity;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.acesso.usuarioLoginTentativa.service.UsuarioLoginTentativaService;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;
import org.springframework.test.context.ActiveProfiles;
import jakarta.servlet.Filter;

@WebMvcTest(
        controllers = AutenticacaoController.class,
        properties = "app.security.swagger-public=false"
)
@ActiveProfiles("test")
@Import({
        ConfigSecurity.class,
        FilterSecurity.class,
        AutenticacaoEntryPoint.class,
        AcessoNegadoHandler.class
})
class ConfigSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @MockitoBean
    private AuthenticationManager manager;

    @MockitoBean
    private TokenSecurity tokenSecurity;

    @MockitoBean
    private SsoSecurity ssoSecurity;

    @MockitoBean
    private UsuarioSessaoService
            usuarioSessaoService;

    @MockitoBean
    private UsuarioLoginTentativaService
            usuarioLoginTentativaService;

    @MockitoBean
    private UsuarioAutenticadoService
            usuarioAutenticadoService;

    @MockitoBean
    private ContextoOrganizacaoService
            contextoOrganizacaoService;

    @MockitoBean
    private AutoridadesOrganizacaoService
            autoridadesOrganizacaoService;

    @MockitoBean
    private AutoridadesPlataformaService
            autoridadesPlataformaService;

    @Test
    @DisplayName(
            "Deve registrar filtros de autenticação e Tenant na ordem correta"
    )
    void deveRegistrarFiltrosDeAutenticacaoETenantNaOrdemCorreta() {
        List<Filter> filtros =
                securityFilterChain.getFilters();

        int indiceAutenticacao =
                localizarFiltro(
                        filtros,
                        FilterSecurity.class
                );

        int indiceAutoridadesPlataforma =
                localizarFiltro(
                        filtros,
                        AutoridadesPlataformaFilter.class
                );

        int indiceContextoOrganizacao =
                localizarFiltro(
                        filtros,
                        ContextoOrganizacaoFilter.class
                );

        assertThat(indiceAutenticacao)
                .isGreaterThanOrEqualTo(0);

        assertThat(indiceAutoridadesPlataforma)
                .isGreaterThan(
                        indiceAutenticacao
                );

        assertThat(indiceContextoOrganizacao)
                .isGreaterThan(
                        indiceAutoridadesPlataforma
                );
    }

    @Test
    @DisplayName(
            "Deve permitir acesso público ao login"
    )
    void devePermitirAcessoPublicoAoLogin()
            throws Exception {
        mockMvc.perform(
                post("/login")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    @DisplayName(
            "Deve permitir acesso público ao refresh token"
    )
    void devePermitirAcessoPublicoAoRefreshToken()
            throws Exception {
        mockMvc.perform(
                post("/login/refresh")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    @DisplayName(
            "Deve permitir acesso público ao logout"
    )
    void devePermitirAcessoPublicoAoLogout()
            throws Exception {
        mockMvc.perform(
                post("/login/logout")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    @DisplayName(
            "Deve permitir acesso público ao login SSO"
    )
    void devePermitirAcessoPublicoAoLoginSso()
            throws Exception {
        mockMvc.perform(
                post("/login/sso")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    @DisplayName(
            "Deve permitir consulta pública de convite"
    )
    void devePermitirConsultaPublicaDeConvite()
            throws Exception {
        mockMvc.perform(
                post(
                        "/plataforma/organizacao/"
                                + "convite/consulta"
                )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    @DisplayName(
            "Deve permitir aceite público de convite para novo usuário"
    )
    void devePermitirAceitePublicoDeConviteParaNovoUsuario()
            throws Exception {
        mockMvc.perform(
                post(
                        "/plataforma/organizacao/"
                                + "convite/aceite/novo-usuario"
                )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isNotFound()
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear endpoint protegido sem autenticação"
    )
    void deveBloquearEndpointProtegidoSemAutenticacao()
            throws Exception {
        mockMvc.perform(
                get("/perfil")
        )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear aceite de convite para usuário existente sem autenticação"
    )
    void deveBloquearAceiteDeConviteParaUsuarioExistenteSemAutenticacao()
            throws Exception {
        mockMvc.perform(
                post(
                        "/plataforma/organizacao/"
                                + "convite/aceite/usuario-existente"
                )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("{}")
        )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );
    }

    @Test
    @DisplayName(
            "Deve bloquear Swagger quando swagger-public estiver false"
    )
    void deveBloquearSwaggerQuandoNaoPublico()
            throws Exception {
        mockMvc.perform(
                get("/swagger-ui/index.html")
        )
                .andExpect(
                        status().isUnauthorized()
                )
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                );
    }

    private int localizarFiltro(
            List<Filter> filtros,
            Class<? extends Filter> tipoFiltro
    ) {
        for (int indice = 0;
             indice < filtros.size();
             indice++) {
            if (tipoFiltro.isInstance(
                    filtros.get(indice)
            )) {
                return indice;
            }
        }

        return -1;
    }
}