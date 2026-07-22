package com.empresa.erp.core.security.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

class AcessoNegadoHandlerTest {

    @Test
    @DisplayName("Deve retornar erro 403 em JSON")
    void deveRetornarErro403EmJson() throws Exception {
        var handler = new AcessoNegadoHandler();
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Acesso negado"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).contains(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");
        assertThat(response.getContentAsString()).contains("\"status\":403");
        assertThat(response.getContentAsString()).contains("\"erro\":\"ACESSO_NEGADO\"");
        assertThat(response.getContentAsString()).contains("\"mensagem\":\"Acesso negado\"");
    }
}