package com.empresa.erp.domain.configuracao.estabelecimento.controller;

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
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.configuracao.estabelecimento.record.AtualizaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.DetalheEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.EstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.record.ListaEstabelecimentoRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.service.EstabelecimentoService;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

class EstabelecimentoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EstabelecimentoService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                EstabelecimentoService.class
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new EstabelecimentoController(service)
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar estabelecimento")
    void deveCadastrarEstabelecimento()
            throws Exception {
        var dados = new EstabelecimentoRecord(
                1L,
                "Filial Curitiba"
        );

        var estabelecimento = criarEstabelecimento();

        when(service.cadastrar(
                any(EstabelecimentoRecord.class)
        )).thenReturn(estabelecimento);

        mockMvc.perform(
                        post("/configuracao/estabelecimento")
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
                                "http://localhost/configuracao/estabelecimento/2"
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
        var dados = new EstabelecimentoRecord(
                null,
                ""
        );

        mockMvc.perform(
                        post("/configuracao/estabelecimento")
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
    @DisplayName("Deve listar estabelecimentos")
    void deveListarEstabelecimentos()
            throws Exception {
        var lista = List.of(
                new ListaEstabelecimentoRecord(
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
                        get("/configuracao/estabelecimento")
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
                new PageImpl<ListaEstabelecimentoRecord>(
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
                        get("/configuracao/estabelecimento")
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
    @DisplayName("Deve detalhar estabelecimento")
    void deveDetalharEstabelecimento()
            throws Exception {
        var detalhe =
                new DetalheEstabelecimentoRecord(
                        2L,
                        1L,
                        "Empresa Exemplo",
                        "Filial Curitiba",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(2L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        get("/configuracao/estabelecimento/2")
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
    @DisplayName("Deve atualizar estabelecimento")
    void deveAtualizarEstabelecimento()
            throws Exception {
        var dados =
                new AtualizaEstabelecimentoRecord(
                        2L,
                        "Filial Atualizada"
                );

        var detalhe =
                new DetalheEstabelecimentoRecord(
                        2L,
                        1L,
                        "Empresa Exemplo",
                        "Filial Atualizada",
                        StatusEnum.ATIVO
                );

        when(service.atualizar(
                any(AtualizaEstabelecimentoRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                        put("/configuracao/estabelecimento")
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
                new AtualizaEstabelecimentoRecord(
                        null,
                        ""
                );

        mockMvc.perform(
                        put("/configuracao/estabelecimento")
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
    @DisplayName("Deve excluir estabelecimento")
    void deveExcluirEstabelecimento()
            throws Exception {
        mockMvc.perform(
                        delete("/configuracao/estabelecimento/2")
                )
                .andExpect(status().isNoContent());

        verify(service).excluir(2L);
    }

    private EstabelecimentoModel criarEstabelecimento() {
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

        var estabelecimento = new EstabelecimentoModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                estabelecimento,
                "id",
                2L
        );

        return estabelecimento;
    }
}