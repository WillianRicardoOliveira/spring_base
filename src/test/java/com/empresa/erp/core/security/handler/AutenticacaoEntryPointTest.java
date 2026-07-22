package com.empresa.erp.core.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class AutenticacaoEntryPointTest {

    @Test
    @DisplayName("Deve retornar erro 401 em JSON")
    void deveRetornarErro401EmJson() throws Exception {
        var entryPoint = new AutenticacaoEntryPoint();
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        entryPoint.commence(request, response, null);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentAsString()).contains("\"status\":401");
        assertThat(response.getContentAsString()).contains("\"erro\":\"NAO_AUTENTICADO\"");
        assertThat(response.getContentAsString()).contains("\"mensagem\":\"Nao autenticado\"");
    }
}