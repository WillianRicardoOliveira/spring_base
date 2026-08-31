package com.empresa.erp.domain.acesso.permissao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.acesso.permissao.record.DetalhePermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.record.ListaPermissaoRecord;
import com.empresa.erp.domain.acesso.permissao.service.PermissaoService;
import com.empresa.erp.domain.base.model.StatusEnum;

class PermissaoControllerTest {

    private MockMvc mockMvc;

    private PermissaoService service;

    @BeforeEach
    void setUp() {
        service =
                org.mockito.Mockito.mock(
                        PermissaoService.class
                );

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new PermissaoController(
                                        service
                                )
                        )
                        .setCustomArgumentResolvers(
                                new PageableHandlerMethodArgumentResolver()
                        )
                        .build();
    }

    @Test
    @DisplayName(
            "Deve listar permissões"
    )
    void deveListarPermissoes()
            throws Exception {
        var lista =
                List.of(
                        new ListaPermissaoRecord(
                                1L,
                                "Listar perfis",
                                "ACESSO_PERFIL_LISTAR",
                                "Permite listar perfis",
                                StatusEnum.ATIVO
                        )
                );

        var pagina =
                new PageImpl<>(
                        lista,
                        PageRequest.of(0, 10),
                        lista.size()
                );

        when(service.listar(
                any(Pageable.class),
                isNull()
        )).thenReturn(pagina);

        mockMvc.perform(
                get("/permissao")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.content[0].nome")
                                .value(
                                        "Listar perfis"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].chave")
                                .value(
                                        "ACESSO_PERFIL_LISTAR"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].descricao")
                                .value(
                                        "Permite listar perfis"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("ATIVO")
                );

        verify(service)
                .listar(
                        any(Pageable.class),
                        isNull()
                );
    }

    @Test
    @DisplayName(
            "Deve listar permissões com filtro"
    )
    void deveListarPermissoesComFiltro()
            throws Exception {
        var lista =
                List.of(
                        new ListaPermissaoRecord(
                                2L,
                                "Criar perfis",
                                "ACESSO_PERFIL_CRIAR",
                                "Permite criar perfis",
                                StatusEnum.ATIVO
                        )
                );

        var pagina =
                new PageImpl<>(
                        lista,
                        PageRequest.of(0, 10),
                        lista.size()
                );

        when(service.listar(
                any(Pageable.class),
                eq("criar")
        )).thenReturn(pagina);

        mockMvc.perform(
                get("/permissao")
                        .param(
                                "filtro",
                                "criar"
                        )
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.content[0].nome")
                                .value(
                                        "Criar perfis"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].chave")
                                .value(
                                        "ACESSO_PERFIL_CRIAR"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].descricao")
                                .value(
                                        "Permite criar perfis"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("ATIVO")
                );

        verify(service)
                .listar(
                        any(Pageable.class),
                        eq("criar")
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar permissão"
    )
    void deveDetalharPermissao()
            throws Exception {
        var detalhe =
                new DetalhePermissaoRecord(
                        1L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR",
                        "Permite listar perfis",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(1L))
                .thenReturn(detalhe);

        mockMvc.perform(
                get("/permissao/1")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value(
                                        "Listar perfis"
                                )
                )
                .andExpect(
                        jsonPath("$.chave")
                                .value(
                                        "ACESSO_PERFIL_LISTAR"
                                )
                )
                .andExpect(
                        jsonPath("$.descricao")
                                .value(
                                        "Permite listar perfis"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .detalhar(1L);
    }
}