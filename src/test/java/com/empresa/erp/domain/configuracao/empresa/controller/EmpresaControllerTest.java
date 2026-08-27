package com.empresa.erp.domain.configuracao.empresa.controller;

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
import com.empresa.erp.domain.configuracao.empresa.record.AtualizaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.DetalheEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.record.ListaEmpresaRecord;
import com.empresa.erp.domain.configuracao.empresa.service.EmpresaService;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

class EmpresaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EmpresaService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                EmpresaService.class
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new EmpresaController(service)
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar empresa e retornar status 201")
    void deveCadastrarEmpresaERetornarStatus201()
            throws Exception {
        var dados =
                new EmpresaRecord("Empresa Exemplo");

        var empresa = criarEmpresa(
                1L,
                "Empresa Exemplo"
        );

        when(service.cadastrar(
                any(EmpresaRecord.class)
        )).thenReturn(empresa);

        mockMvc.perform(
                        post("/configuracao/empresa")
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
                                "http://localhost/configuracao/empresa/1"
                        )
                )
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(
                        jsonPath("$.nome")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve rejeitar cadastro com nome em branco")
    void deveRejeitarCadastroComNomeEmBranco()
            throws Exception {
        var dados = new EmpresaRecord("");

        mockMvc.perform(
                        post("/configuracao/empresa")
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
    @DisplayName("Deve rejeitar cadastro com nome acima do limite")
    void deveRejeitarCadastroComNomeAcimaDoLimite()
            throws Exception {
        var dados =
                new EmpresaRecord("A".repeat(101));

        mockMvc.perform(
                        post("/configuracao/empresa")
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
    @DisplayName("Deve listar empresas")
    void deveListarEmpresas() throws Exception {
        var lista = List.of(
                new ListaEmpresaRecord(
                        1L,
                        "Empresa Exemplo",
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
                isNull()
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/configuracao/empresa")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.content[0].nome")
                                .value("Empresa Exemplo")
                );

        verify(service).listar(
                any(Pageable.class),
                isNull()
        );
    }

    @Test
    @DisplayName("Deve listar empresas com filtro")
    void deveListarEmpresasComFiltro()
            throws Exception {
        var lista = List.of(
                new ListaEmpresaRecord(
                        1L,
                        "Empresa Exemplo",
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
                eq("Empresa")
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/configuracao/empresa")
                                .param(
                                        "filtro",
                                        "Empresa"
                                )
                )
                .andExpect(status().isOk());

        verify(service).listar(
                any(Pageable.class),
                eq("Empresa")
        );
    }

    @Test
    @DisplayName("Deve detalhar empresa")
    void deveDetalharEmpresa() throws Exception {
        var detalhe = new DetalheEmpresaRecord(
                1L,
                "Empresa Exemplo",
                StatusEnum.ATIVO
        );

        when(service.detalhar(1L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        get("/configuracao/empresa/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(
                        jsonPath("$.nome")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve atualizar empresa")
    void deveAtualizarEmpresa() throws Exception {
        var dados = new AtualizaEmpresaRecord(
                1L,
                "Empresa Atualizada"
        );

        var detalhe = new DetalheEmpresaRecord(
                1L,
                "Empresa Atualizada",
                StatusEnum.ATIVO
        );

        when(service.atualizar(
                any(AtualizaEmpresaRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                        put("/configuracao/empresa")
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
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(
                        jsonPath("$.nome")
                                .value("Empresa Atualizada")
                );
    }

    @Test
    @DisplayName("Deve rejeitar atualização sem id")
    void deveRejeitarAtualizacaoSemId()
            throws Exception {
        var dados = new AtualizaEmpresaRecord(
                null,
                "Empresa Atualizada"
        );

        mockMvc.perform(
                        put("/configuracao/empresa")
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
    @DisplayName("Deve rejeitar atualização com nome em branco")
    void deveRejeitarAtualizacaoComNomeEmBranco()
            throws Exception {
        var dados =
                new AtualizaEmpresaRecord(1L, "");

        mockMvc.perform(
                        put("/configuracao/empresa")
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
    @DisplayName("Deve excluir empresa e retornar status 204")
    void deveExcluirEmpresaERetornarStatus204()
            throws Exception {
        mockMvc.perform(
                        delete("/configuracao/empresa/1")
                )
                .andExpect(status().isNoContent());

        verify(service).excluir(1L);
    }

    private EmpresaModel criarEmpresa(
            Long id,
            String nome
    ) {
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
                new EmpresaRecord(nome)
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                id
        );

        return empresa;
    }
}