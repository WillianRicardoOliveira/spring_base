package com.empresa.erp.domain.organizacao.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.organizacao.record.OrganizacaoDisponivelRecord;
import com.empresa.erp.domain.organizacao.service.OrganizacaoDisponivelService;

class OrganizacaoControllerTest {

    private MockMvc mockMvc;

    private OrganizacaoDisponivelService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                OrganizacaoDisponivelService.class
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new OrganizacaoController(
                                service
                        )
                )
                .build();
    }

    @Test
    @DisplayName("Deve listar organizações disponíveis")
    void deveListarOrganizacoesDisponiveis()
            throws Exception {
        when(service.listar()).thenReturn(
                List.of(
                        new OrganizacaoDisponivelRecord(
                                10L,
                                "Organização A"
                        ),
                        new OrganizacaoDisponivelRecord(
                                20L,
                                "Organização B"
                        )
                )
        );

        mockMvc.perform(
                        get(
                                "/organizacao/disponiveis"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$[0].nome")
                                .value("Organização A")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(20L)
                )
                .andExpect(
                        jsonPath("$[1].nome")
                                .value("Organização B")
                );

        verify(service).listar();
    }

    @Test
    @DisplayName("Deve retornar lista vazia sem organizações")
    void deveRetornarListaVaziaSemOrganizacoes()
            throws Exception {
        when(service.listar()).thenReturn(List.of());

        mockMvc.perform(
                        get(
                                "/organizacao/disponiveis"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$").isArray()
                )
                .andExpect(
                        jsonPath("$").isEmpty()
                );

        verify(service).listar();
    }

    @Test
    @DisplayName(
            "Não deve exigir permissão funcional "
                    + "para listar organizações"
    )
    void naoDeveExigirPermissaoFuncionalParaListarOrganizacoes()
            throws Exception {
        var metodo = OrganizacaoController.class
                .getDeclaredMethod(
                        "listarDisponiveis"
                );

        assertThat(
                metodo.getAnnotation(
                        PreAuthorize.class
                )
        ).isNull();
    }
}