package com.empresa.erp.domain.plataforma.organizacao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.plataforma.organizacao.record.DetalheOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.ListaOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.record.OrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.service.OrganizacaoPlataformaService;
import com.fasterxml.jackson.databind.ObjectMapper;

class OrganizacaoPlataformaControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private OrganizacaoPlataformaService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                OrganizacaoPlataformaService.class
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new OrganizacaoPlataformaController(
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
            "Deve listar organizações"
    )
    void deveListarOrganizacoes()
            throws Exception {
        var lista =
                List.of(
                        new ListaOrganizacaoRecord(
                                10L,
                                "Organização A",
                                StatusEnum.ATIVO
                        ),
                        new ListaOrganizacaoRecord(
                                20L,
                                "Organização B",
                                StatusEnum.INATIVO
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
                        get(
                                "/plataforma/organizacao"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.content[0].nome")
                                .value("Organização A")
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("ATIVO")
                )
                .andExpect(
                        jsonPath("$.content[1].id")
                                .value(20L)
                )
                .andExpect(
                        jsonPath("$.content[1].status")
                                .value("INATIVO")
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(2)
                );

        verify(service).listar(
                any(Pageable.class),
                isNull()
        );
    }

    @Test
    @DisplayName(
            "Deve listar organizações com filtro"
    )
    void deveListarOrganizacoesComFiltro()
            throws Exception {
        var pagina =
                new PageImpl<ListaOrganizacaoRecord>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(service.listar(
                any(Pageable.class),
                eq("Matriz")
        )).thenReturn(pagina);

        mockMvc.perform(
                        get(
                                "/plataforma/organizacao"
                        )
                                .param(
                                        "filtro",
                                        "Matriz"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content")
                                .isEmpty()
                );

        verify(service).listar(
                any(Pageable.class),
                eq("Matriz")
        );
    }

    @Test
    @DisplayName(
            "Deve detalhar organização"
    )
    void deveDetalharOrganizacao()
            throws Exception {
        var detalhe =
                new DetalheOrganizacaoRecord(
                        10L,
                        "Organização A",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(10L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        get(
                                "/plataforma/organizacao/10"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value("Organização A")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service).detalhar(10L);
    }

    @Test
    @DisplayName(
            "Deve editar organização"
    )
    void deveEditarOrganizacao()
            throws Exception {
        var dados =
                new OrganizacaoRecord(
                        "Novo nome"
                );

        var detalhe =
                new DetalheOrganizacaoRecord(
                        10L,
                        "Novo nome",
                        StatusEnum.ATIVO
                );

        when(service.editar(
                eq(10L),
                any(OrganizacaoRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                        put(
                                "/plataforma/organizacao/10"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        dados
                                                )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value("Novo nome")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service).editar(
                eq(10L),
                any(OrganizacaoRecord.class)
        );
    }

    @Test
    @DisplayName(
            "Deve rejeitar edição com nome em branco"
    )
    void deveRejeitarEdicaoComNomeEmBranco()
            throws Exception {
        var dados =
                new OrganizacaoRecord("");

        mockMvc.perform(
                        put(
                                "/plataforma/organizacao/10"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        dados
                                                )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName(
            "Deve inativar organização"
    )
    void deveInativarOrganizacao()
            throws Exception {
        var detalhe =
                new DetalheOrganizacaoRecord(
                        10L,
                        "Organização",
                        StatusEnum.INATIVO
                );

        when(service.inativar(10L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        patch(
                                "/plataforma/organizacao/"
                                        + "10/inativar"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("INATIVO")
                );

        verify(service).inativar(10L);
    }

    @Test
    @DisplayName(
            "Deve reativar organização"
    )
    void deveReativarOrganizacao()
            throws Exception {
        var detalhe =
                new DetalheOrganizacaoRecord(
                        10L,
                        "Organização",
                        StatusEnum.ATIVO
                );

        when(service.reativar(10L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        patch(
                                "/plataforma/organizacao/"
                                        + "10/reativar"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service).reativar(10L);
    }

    @Test
    @DisplayName(
            "Deve remover organização"
    )
    void deveRemoverOrganizacao()
            throws Exception {
        mockMvc.perform(
                        delete(
                                "/plataforma/organizacao/10"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(service).remover(10L);
    }
}