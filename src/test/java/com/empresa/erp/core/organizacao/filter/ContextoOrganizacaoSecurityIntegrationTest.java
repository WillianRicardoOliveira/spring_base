package com.empresa.erp.core.organizacao.filter;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.erp.core.exception.TratarErros;
import com.empresa.erp.core.organizacao.service.ContextoOrganizacaoService;
import com.empresa.erp.core.security.config.ConfigSecurity;
import com.empresa.erp.core.security.filter.FilterSecurity;
import com.empresa.erp.core.security.handler.AcessoNegadoHandler;
import com.empresa.erp.core.security.handler.AutenticacaoEntryPoint;
import com.empresa.erp.core.security.jwt.TokenSecurity;
import com.empresa.erp.core.security.model.UsuarioAutenticado;
import com.empresa.erp.core.security.record.AccessTokenValidadoSecurity;
import com.empresa.erp.core.security.service.AutoridadesOrganizacaoService;
import com.empresa.erp.core.security.service.AutoridadesPlataformaService;
import com.empresa.erp.core.security.service.UsuarioAutenticadoService;
import com.empresa.erp.domain.acesso.usuarioSessao.service.UsuarioSessaoService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;

@WebMvcTest(
        controllers = {
                ContextoOrganizacaoSecurityIntegrationTest
                        .OperacionalControllerTeste.class,
                ContextoOrganizacaoSecurityIntegrationTest
                        .PlataformaControllerTeste.class
        },
        properties = "app.security.swagger-public=false"
)
@Import({
        ConfigSecurity.class,
        FilterSecurity.class,
        AutenticacaoEntryPoint.class,
        AcessoNegadoHandler.class,
        TratarErros.class
})
class ContextoOrganizacaoSecurityIntegrationTest {

    private static final Long ID_USUARIO = 10L;
    private static final Long ID_ORGANIZACAO = 20L;

    private static final String TOKEN = "token-valido";
    private static final String JTI = "jti-valido";
    private static final String EMAIL = "admin@teste.com";

    private static final String PERMISSAO_ORGANIZACIONAL =
            "TENANT_TESTE";

    private static final String PERMISSAO_PLATAFORMA =
            "PLATAFORMA_TESTE";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenSecurity tokenSecurity;

    @MockitoBean
    private UsuarioSessaoService usuarioSessaoService;

    @MockitoBean
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @MockitoBean
    private ContextoOrganizacaoService contextoOrganizacaoService;

    @MockitoBean
    private AutoridadesOrganizacaoService autoridadesOrganizacaoService;

    @MockitoBean
    private AutoridadesPlataformaService autoridadesPlataformaService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        when(tokenSecurity.validarAccessToken(TOKEN))
                .thenReturn(
                        new AccessTokenValidadoSecurity(
                                EMAIL,
                                JTI
                        )
                );

        when(usuarioSessaoService.accessTokenEstaAtivo(JTI))
                .thenReturn(true);

        when(usuarioAutenticadoService.buscarPorEmail(EMAIL))
                .thenReturn(usuarioAutenticado());

        when(autoridadesPlataformaService.buscar(ID_USUARIO))
                .thenReturn(List.of());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName(
            "Deve bloquear endpoint operacional sem header de organizacao"
    )
    void deveBloquearEndpointOperacionalSemHeaderDeOrganizacao()
            throws Exception {
        mockMvc.perform(
                getAutenticado("/operacional/teste")
        )
                .andExpect(status().isForbidden())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("ACESSO_NEGADO"));

        verifyNoInteractions(
                contextoOrganizacaoService,
                autoridadesOrganizacaoService
        );
    }

    @Test
    @DisplayName(
            "Deve retornar 400 para header de organizacao invalido"
    )
    void deveRetornar400ParaHeaderDeOrganizacaoInvalido()
            throws Exception {
        mockMvc.perform(
                getAutenticado("/operacional/teste")
                        .header(
                                ContextoOrganizacaoFilter
                                        .HEADER_ORGANIZACAO,
                                "abc"
                        )
        )
                .andExpect(status().isBadRequest())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("REGRA_DE_NEGOCIO"))
                .andExpect(
                        jsonPath("$.mensagem")
                                .value("Organizacao invalida.")
                );

        verifyNoInteractions(
                contextoOrganizacaoService,
                autoridadesOrganizacaoService
        );
    }

    @Test
    @DisplayName(
            "Deve retornar 403 quando usuario nao possui vinculo ativo"
    )
    void deveRetornar403QuandoUsuarioNaoPossuiVinculoAtivo()
            throws Exception {
        doThrow(new AccessDeniedException("Acesso negado."))
                .when(contextoOrganizacaoService)
                .definir(ID_ORGANIZACAO);

        mockMvc.perform(
                getAutenticado("/operacional/teste")
                        .header(
                                ContextoOrganizacaoFilter
                                        .HEADER_ORGANIZACAO,
                                ID_ORGANIZACAO
                        )
        )
                .andExpect(status().isForbidden())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("ACESSO_NEGADO"));

        verify(contextoOrganizacaoService)
                .definir(ID_ORGANIZACAO);

        verifyNoInteractions(autoridadesOrganizacaoService);
    }

    @Test
    @DisplayName(
            "Deve permitir endpoint operacional com organizacao valida "
                    + "e permissao organizacional"
    )
    void devePermitirEndpointOperacionalComOrganizacaoValidaEPermissao()
            throws Exception {
        when(autoridadesOrganizacaoService.buscar(
                ID_USUARIO,
                ID_ORGANIZACAO
        )).thenReturn(
                List.of(
                        new SimpleGrantedAuthority(
                                PERMISSAO_ORGANIZACIONAL
                        )
                )
        );

        mockMvc.perform(
                getAutenticado("/operacional/teste")
                        .header(
                                ContextoOrganizacaoFilter
                                        .HEADER_ORGANIZACAO,
                                ID_ORGANIZACAO
                        )
        )
                .andExpect(status().isOk())
                .andExpect(content().string("operacional-ok"));

        verify(contextoOrganizacaoService)
                .definir(ID_ORGANIZACAO);

        verify(autoridadesOrganizacaoService)
                .buscar(
                        ID_USUARIO,
                        ID_ORGANIZACAO
                );
    }

    @Test
    @DisplayName(
            "Deve ignorar contexto de organizacao em endpoint da plataforma"
    )
    void deveIgnorarContextoDeOrganizacaoEmEndpointDaPlataforma()
            throws Exception {
        when(autoridadesPlataformaService.buscar(ID_USUARIO))
                .thenReturn(
                        List.of(
                                new SimpleGrantedAuthority(
                                        PERMISSAO_PLATAFORMA
                                )
                        )
                );

        mockMvc.perform(
                getAutenticado("/plataforma/teste")
                        .header(
                                ContextoOrganizacaoFilter
                                        .HEADER_ORGANIZACAO,
                                "abc"
                        )
        )
                .andExpect(status().isOk())
                .andExpect(content().string("plataforma-ok"));

        verifyNoInteractions(
                contextoOrganizacaoService,
                autoridadesOrganizacaoService
        );
    }

    private MockHttpServletRequestBuilder getAutenticado(
            String endpoint
    ) {
        return get(endpoint)
                .header(
                        "Authorization",
                        "Bearer " + TOKEN
                );
    }

    private UsuarioAutenticado usuarioAutenticado() {
        var usuario =
                new UsuarioModel();

        ReflectionTestUtils.setField(
                usuario,
                "id",
                ID_USUARIO
        );

        ReflectionTestUtils.setField(
                usuario,
                "email",
                EMAIL
        );

        ReflectionTestUtils.setField(
                usuario,
                "status",
                StatusEnum.ATIVO
        );

        return new UsuarioAutenticado(
                usuario,
                List.of()
        );
    }

    @RestController
    @RequestMapping("/operacional/teste")
    public static class OperacionalControllerTeste {

        @GetMapping
        @PreAuthorize("hasAuthority('TENANT_TESTE')")
        public String consultar() {
            return "operacional-ok";
        }
    }

    @RestController
    @RequestMapping("/plataforma/teste")
    public static class PlataformaControllerTeste {

        @GetMapping
        @PreAuthorize("hasAuthority('PLATAFORMA_TESTE')")
        public String consultar() {
            return "plataforma-ok";
        }
    }
}