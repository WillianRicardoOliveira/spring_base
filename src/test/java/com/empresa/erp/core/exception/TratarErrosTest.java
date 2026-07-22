package com.empresa.erp.core.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

class TratarErrosTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ControllerTeste())
                .setControllerAdvice(new TratarErros())
                .build();
    }

    @Test
    @DisplayName("Deve retornar 404 em JSON quando entidade nao for encontrada")
    void deveRetornar404EmJsonQuandoEntidadeNaoForEncontrada() throws Exception {
        mockMvc.perform(get("/teste/entity-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("NAO_ENCONTRADO"))
                .andExpect(jsonPath("$.mensagem").value("Recurso nao encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 400 em JSON com erros de validacao")
    void deveRetornar400EmJsonComErrosDeValidacao() throws Exception {
        var json = """
                {
                    "nome": ""
                }
                """;

        mockMvc.perform(post("/teste/validacao-bean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("VALIDACAO"))
                .andExpect(jsonPath("$.mensagem").value("Dados invalidos"))
                .andExpect(jsonPath("$.campos[0].campo").value("nome"))
                .andExpect(jsonPath("$.campos[0].mensagem").value("nome obrigatorio"));
    }

    @Test
    @DisplayName("Deve retornar 400 em JSON quando JSON for invalido")
    void deveRetornar400EmJsonQuandoJsonForInvalido() throws Exception {
        mockMvc.perform(post("/teste/validacao-bean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.mensagem").value("Corpo da requisicao invalido"));
    }

    @Test
    @DisplayName("Deve retornar 400 em JSON para regra de negocio")
    void deveRetornar400EmJsonParaRegraDeNegocio() throws Exception {
        mockMvc.perform(get("/teste/validacao-negocio"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("REGRA_DE_NEGOCIO"))
                .andExpect(jsonPath("$.mensagem").value("Regra de negocio invalida"));
    }

    @Test
    @DisplayName("Deve retornar 401 em JSON para refresh token invalido")
    void deveRetornar401EmJsonParaRefreshTokenInvalido() throws Exception {
        mockMvc.perform(get("/teste/refresh-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("REFRESH_TOKEN_INVALIDO"))
                .andExpect(jsonPath("$.mensagem").value("Refresh token invalido."));
    }

    @Test
    @DisplayName("Deve retornar 401 em JSON para credenciais invalidas")
    void deveRetornar401EmJsonParaCredenciaisInvalidas() throws Exception {
        mockMvc.perform(get("/teste/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("NAO_AUTENTICADO"))
                .andExpect(jsonPath("$.mensagem").value("Credenciais invalidas"));
    }

    @Test
    @DisplayName("Deve retornar 401 em JSON para falha de autenticacao")
    void deveRetornar401EmJsonParaFalhaDeAutenticacao() throws Exception {
        mockMvc.perform(get("/teste/authentication"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("NAO_AUTENTICADO"))
                .andExpect(jsonPath("$.mensagem").value("Falha na autenticacao"));
    }

    @Test
    @DisplayName("Deve retornar 403 em JSON para acesso negado")
    void deveRetornar403EmJsonParaAcessoNegado() throws Exception {
        mockMvc.perform(get("/teste/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("ACESSO_NEGADO"))
                .andExpect(jsonPath("$.mensagem").value("Acesso negado"));
    }

    @Test
    @DisplayName("Deve retornar 401 em JSON para erro de SSO")
    void deveRetornar401EmJsonParaErroDeSso() throws Exception {
        mockMvc.perform(get("/teste/sso"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("SSO_INVALIDO"))
                .andExpect(jsonPath("$.mensagem").value("Token SSO invalido"));
    }

    @Test
    @DisplayName("Deve retornar 500 em JSON sem expor detalhes internos")
    void deveRetornar500EmJsonSemExporDetalhesInternos() throws Exception {
        mockMvc.perform(get("/teste/erro-inesperado"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.erro").value("ERRO_INTERNO"))
                .andExpect(jsonPath("$.mensagem").value("Erro interno do servidor"))
                .andExpect(content().string(not(containsString("Erro inesperado"))));
    }

    @RestController
    private static class ControllerTeste {

        @GetMapping("/teste/entity-not-found")
        void entityNotFound() {
            throw new EntityNotFoundException();
        }

        @PostMapping("/teste/validacao-bean")
        void validacaoBean(@RequestBody @Valid DadosTeste dados) {
        }

        @GetMapping("/teste/validacao-negocio")
        void validacaoNegocio() {
            throw new ValidacaoException("Regra de negocio invalida");
        }

        @GetMapping("/teste/refresh-token")
        void refreshToken() {
            throw new RefreshTokenException("Refresh token invalido.");
        }

        @GetMapping("/teste/bad-credentials")
        void badCredentials() {
            throw new BadCredentialsException("senha invalida");
        }

        @GetMapping("/teste/authentication")
        void authentication() {
            throw new AuthenticationServiceException("falha");
        }

        @GetMapping("/teste/access-denied")
        void accessDenied() {
            throw new AccessDeniedException("sem acesso");
        }

        @GetMapping("/teste/sso")
        void sso() {
            throw new SsoAuthenticationException("Token SSO invalido");
        }

        @GetMapping("/teste/erro-inesperado")
        void erroInesperado() {
            throw new RuntimeException("Erro inesperado");
        }
    }

    private record DadosTeste(
            @NotBlank(message = "nome obrigatorio")
            String nome
    ) {
    }
}