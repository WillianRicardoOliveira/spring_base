package com.empresa.erp.domain.configuracao.inicial.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.configuracao.inicial.model.ProximaEtapaConfiguracaoEnum;
import com.empresa.erp.domain.configuracao.inicial.record.EstadoConfiguracaoInicialRecord;
import com.empresa.erp.domain.configuracao.inicial.service.ConfiguracaoInicialService;

class ConfiguracaoInicialControllerTest {

    private MockMvc mockMvc;

    private ConfiguracaoInicialService
            service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                ConfiguracaoInicialService.class
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new ConfiguracaoInicialController(
                                service
                        )
                )
                .build();
    }

    @Test
    @DisplayName(
            "Deve informar empresa como proxima etapa"
    )
    void deveInformarEmpresaComoProximaEtapa()
            throws Exception {

        when(
                service.consultar()
        ).thenReturn(
                new EstadoConfiguracaoInicialRecord(
                        false,
                        ProximaEtapaConfiguracaoEnum
                                .EMPRESA
                )
        );

        mockMvc.perform(
                        get("/configuracao/inicial")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.empresaCadastrada"
                        ).value(false)
                )
                .andExpect(
                        jsonPath(
                                "$.proximaEtapa"
                        ).value("EMPRESA")
                );

        verify(service).consultar();
    }

    @Test
    @DisplayName(
            "Deve informar empresa cadastrada"
    )
    void deveInformarEmpresaCadastrada()
            throws Exception {

        when(
                service.consultar()
        ).thenReturn(
                new EstadoConfiguracaoInicialRecord(
                        true,
                        null
                )
        );

        mockMvc.perform(
                        get("/configuracao/inicial")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.empresaCadastrada"
                        ).value(true)
                )
                .andExpect(
                        jsonPath(
                                "$.proximaEtapa"
                        ).doesNotExist()
                );

        verify(service).consultar();
    }
}