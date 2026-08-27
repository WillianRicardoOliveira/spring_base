package com.empresa.erp.domain.configuracao.subsidiaria.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.model.SubsidiariaModel;
import com.empresa.erp.domain.configuracao.subsidiaria.record.AtualizaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.DetalheSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.ListaSubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.record.SubsidiariaRecord;
import com.empresa.erp.domain.configuracao.subsidiaria.service.SubsidiariaService;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

class SubsidiariaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private SubsidiariaService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                SubsidiariaService.class
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new SubsidiariaController(service)
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar subsidiaria")
    void deveCadastrarSubsidiaria()
            throws Exception {
        var dados = new SubsidiariaRecord(
                1L,
                "Filial Curitiba"
        );

        var subsidiaria = criarSubsidiaria();

        when(service.cadastrar(
                any(SubsidiariaRecord.class)
        )).thenReturn(subsidiaria);

        mockMvc.perform(
                        post("/configuracao/subsidiaria")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                dados
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/configuracao/subsidiaria/2"
                        )
                )
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(
                        jsonPath("$.idEmpresa")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.empresa")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value("Filial Curitiba")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve rejeitar cadastro invalido")
    void deveRejeitarCadastroInvalido()
            throws Exception {
        var dados = new SubsidiariaRecord(
                null,
                ""
        );

        mockMvc.perform(
                        post("/configuracao/subsidiaria")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                dados
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar subsidiarias")
    void deveListarSubsidiarias()
            throws Exception {
        var lista = List.of(
                new ListaSubsidiariaRecord(
                        2L,
                        1L,
                        "Empresa Exemplo",
                        "Filial Curitiba",
                        StatusEnum.ATIVO
                )
        );

        var pagina = new PageImpl<>(
                lista,
                PageRequest.of(0, 10),
                lista.size()
        );

        when(service.listar(
                any(Pageable.class),
                isNull(),
                isNull()
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/configuracao/subsidiaria")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.content[0].idEmpresa")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.content[0].empresa")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.content[0].nome")
                                .value("Filial Curitiba")
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("ATIVO")
                );

        verify(service).listar(
                any(Pageable.class),
                isNull(),
                isNull()
        );
    }

    @Test
    @DisplayName("Deve listar com empresa e filtro")
    void deveListarComEmpresaEFiltro()
            throws Exception {
        var pagina =
                new PageImpl<ListaSubsidiariaRecord>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(service.listar(
                any(Pageable.class),
                eq(1L),
                eq("Curitiba")
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/configuracao/subsidiaria")
                                .param(
                                        "idEmpresa",
                                        "1"
                                )
                                .param(
                                        "filtro",
                                        "Curitiba"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content")
                                .isEmpty()
                );

        verify(service).listar(
                any(Pageable.class),
                eq(1L),
                eq("Curitiba")
        );
    }

    @Test
    @DisplayName("Deve detalhar subsidiaria")
    void deveDetalharSubsidiaria()
            throws Exception {
        var detalhe =
                new DetalheSubsidiariaRecord(
                        2L,
                        1L,
                        "Empresa Exemplo",
                        "Filial Curitiba",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(2L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        get("/configuracao/subsidiaria/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(
                        jsonPath("$.idEmpresa")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.empresa")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value("Filial Curitiba")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve atualizar subsidiaria")
    void deveAtualizarSubsidiaria()
            throws Exception {
        var dados =
                new AtualizaSubsidiariaRecord(
                        2L,
                        "Filial Atualizada"
                );

        var detalhe =
                new DetalheSubsidiariaRecord(
                        2L,
                        1L,
                        "Empresa Exemplo",
                        "Filial Atualizada",
                        StatusEnum.ATIVO
                );

        when(service.atualizar(
                any(AtualizaSubsidiariaRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                        put("/configuracao/subsidiaria")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                dados
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(
                        jsonPath("$.idEmpresa")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value("Filial Atualizada")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve rejeitar atualizacao invalida")
    void deveRejeitarAtualizacaoInvalida()
            throws Exception {
        var dados =
                new AtualizaSubsidiariaRecord(
                        null,
                        ""
                );

        mockMvc.perform(
                        put("/configuracao/subsidiaria")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                dados
                                        )
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve excluir subsidiaria")
    void deveExcluirSubsidiaria()
            throws Exception {
        mockMvc.perform(
                        delete("/configuracao/subsidiaria/2")
                )
                .andExpect(status().isNoContent());

        verify(service).excluir(2L);
    }

    private SubsidiariaModel criarSubsidiaria() {
        var organizacao =
                new OrganizacaoModel(
                        "Organizacao Principal"
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                1L
        );

        var empresa = new EmpresaModel(
                organizacao,
                new EmpresaRecord(
                        "Empresa Exemplo"
                )
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                1L
        );

        var subsidiaria = new SubsidiariaModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                subsidiaria,
                "id",
                2L
        );

        return subsidiaria;
    }
}