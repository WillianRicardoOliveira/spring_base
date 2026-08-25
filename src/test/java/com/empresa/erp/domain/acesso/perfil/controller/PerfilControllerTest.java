package com.empresa.erp.domain.acesso.perfil.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.AtualizaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.DetalhePerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.ListaPerfilRecord;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.perfil.service.PerfilService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

class PerfilControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private PerfilService service;

    @BeforeEach
    void setUp() {
        service =
                org.mockito.Mockito.mock(
                        PerfilService.class
                );

        objectMapper =
                new ObjectMapper();

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new PerfilController(
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
            "Deve cadastrar perfil e retornar status 201"
    )
    void deveCadastrarPerfilERetornarStatus201()
            throws Exception {
        PerfilRecord dados =
                new PerfilRecord(
                        "Financeiro",
                        "Perfil do setor financeiro"
                );

        PerfilModel perfil =
                criarPerfil(
                        1L,
                        "Financeiro",
                        "Perfil do setor financeiro",
                        StatusEnum.ATIVO
                );

        when(service.cadastrar(
                any(PerfilRecord.class)
        )).thenReturn(perfil);

        mockMvc.perform(
                post("/perfil")
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
                        status().isCreated()
                )
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/perfil/1"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value("Financeiro")
                )
                .andExpect(
                        jsonPath("$.descricao")
                                .value(
                                        "Perfil do setor financeiro"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .cadastrar(
                        any(PerfilRecord.class)
                );
    }

    @Test
    @DisplayName(
            "Deve retornar 400 ao cadastrar perfil sem nome"
    )
    void deveRetornar400AoCadastrarPerfilSemNome()
            throws Exception {
        PerfilRecord dados =
                new PerfilRecord(
                        "",
                        "Descrição"
                );

        mockMvc.perform(
                post("/perfil")
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
            "Deve listar perfis"
    )
    void deveListarPerfis()
            throws Exception {
        var lista =
                List.of(
                        new ListaPerfilRecord(
                                1L,
                                "Financeiro",
                                "Perfil financeiro",
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
                get("/perfil")
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
                                .value("Financeiro")
                )
                .andExpect(
                        jsonPath("$.content[0].descricao")
                                .value(
                                        "Perfil financeiro"
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
            "Deve listar perfis com filtro"
    )
    void deveListarPerfisComFiltro()
            throws Exception {
        var lista =
                List.of(
                        new ListaPerfilRecord(
                                2L,
                                "Financeiro",
                                "Perfil financeiro",
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
                eq("fin")
        )).thenReturn(pagina);

        mockMvc.perform(
                get("/perfil")
                        .param(
                                "filtro",
                                "fin"
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
                                .value("Financeiro")
                );

        verify(service)
                .listar(
                        any(Pageable.class),
                        eq("fin")
                );
    }

    @Test
    @DisplayName(
            "Deve atualizar perfil"
    )
    void deveAtualizarPerfil()
            throws Exception {
        AtualizaPerfilRecord dados =
                new AtualizaPerfilRecord(
                        1L,
                        "Financeiro atualizado",
                        "Descrição atualizada"
                );

        DetalhePerfilRecord detalhe =
                new DetalhePerfilRecord(
                        1L,
                        "Financeiro atualizado",
                        "Descrição atualizada",
                        StatusEnum.ATIVO
                );

        when(service.atualizar(
                any(AtualizaPerfilRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                put("/perfil")
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
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value(
                                        "Financeiro atualizado"
                                )
                )
                .andExpect(
                        jsonPath("$.descricao")
                                .value(
                                        "Descrição atualizada"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .atualizar(
                        any(AtualizaPerfilRecord.class)
                );
    }

    @Test
    @DisplayName(
            "Deve retornar 400 ao atualizar perfil sem nome"
    )
    void deveRetornar400AoAtualizarPerfilSemNome()
            throws Exception {
        AtualizaPerfilRecord dados =
                new AtualizaPerfilRecord(
                        1L,
                        "",
                        "Descrição"
                );

        mockMvc.perform(
                put("/perfil")
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
            "Deve excluir perfil e retornar status 204"
    )
    void deveExcluirPerfilERetornarStatus204()
            throws Exception {
        mockMvc.perform(
                delete("/perfil/1")
        )
                .andExpect(
                        status().isNoContent()
                );

        verify(service)
                .excluir(1L);
    }

    @Test
    @DisplayName(
            "Deve detalhar perfil"
    )
    void deveDetalharPerfil()
            throws Exception {
        DetalhePerfilRecord detalhe =
                new DetalhePerfilRecord(
                        1L,
                        "Financeiro",
                        "Perfil financeiro",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(1L))
                .thenReturn(detalhe);

        mockMvc.perform(
                get("/perfil/1")
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
                                .value("Financeiro")
                )
                .andExpect(
                        jsonPath("$.descricao")
                                .value(
                                        "Perfil financeiro"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .detalhar(1L);
    }

    private PerfilModel criarPerfil(
            Long id,
            String nome,
            String descricao,
            StatusEnum status
    ) {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        return new PerfilModel(
                id,
                organizacao,
                nome,
                descricao,
                null,
                status
        );
    }
}