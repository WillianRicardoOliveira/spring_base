package com.empresa.erp.core.exception;

import static org.hamcrest.Matchers.containsString;
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
    @DisplayName("Deve retornar 404 quando entidade nao for encontrada")
    void deveRetornar404QuandoEntidadeNaoForEncontrada() throws Exception {
        mockMvc.perform(get("/teste/entity-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Deve retornar 400 com erros de validacao")
    void deveRetornar400ComErrosDeValidacao() throws Exception {
        var json = """
                {
                    "nome": ""
                }
                """;

        mockMvc.perform(post("/teste/validacao-bean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].campo").value("nome"))
                .andExpect(jsonPath("$[0].mensagem").value("nome obrigatorio"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON for invalido")
    void deveRetornar400QuandoJsonForInvalido() throws Exception {
        mockMvc.perform(post("/teste/validacao-bean")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("JSON")));
    }

    @Test
    @DisplayName("Deve retornar 400 para regra de negocio")
    void deveRetornar400ParaRegraDeNegocio() throws Exception {
        mockMvc.perform(get("/teste/validacao-negocio"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Regra de negocio invalida"));
    }

    @Test
    @DisplayName("Deve retornar 401 para credenciais invalidas")
    void deveRetornar401ParaCredenciaisInvalidas() throws Exception {
        mockMvc.perform(get("/teste/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciais invalidas"));
    }

    @Test
    @DisplayName("Deve retornar 401 para falha de autenticacao")
    void deveRetornar401ParaFalhaDeAutenticacao() throws Exception {
        mockMvc.perform(get("/teste/authentication"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Falha na autenticacao"));
    }

    @Test
    @DisplayName("Deve retornar 403 para acesso negado")
    void deveRetornar403ParaAcessoNegado() throws Exception {
        mockMvc.perform(get("/teste/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Acesso negado"));
    }

    @Test
    @DisplayName("Deve retornar 401 para erro de SSO")
    void deveRetornar401ParaErroDeSso() throws Exception {
        mockMvc.perform(get("/teste/sso"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token SSO invalido"));
    }

    @Test
    @DisplayName("Deve retornar 500 para erro inesperado")
    void deveRetornar500ParaErroInesperado() throws Exception {
        mockMvc.perform(get("/teste/erro-inesperado"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro: Erro inesperado"));
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