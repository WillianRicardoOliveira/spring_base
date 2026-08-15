package com.empresa.erp.domain.acesso.usuarioEmpresa.controller;

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

import com.empresa.erp.domain.acesso.usuarioEmpresa.model.UsuarioEmpresaModel;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.AtualizaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.DetalheUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.ListaUsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.record.UsuarioEmpresaRecord;
import com.empresa.erp.domain.acesso.usuarioEmpresa.service.UsuarioEmpresaService;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

class UsuarioEmpresaControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UsuarioEmpresaService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                UsuarioEmpresaService.class
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new UsuarioEmpresaController(service)
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar vinculo")
    void deveCadastrarVinculo() throws Exception {
        var dados = new UsuarioEmpresaRecord(
                1L,
                2L,
                true
        );

        var usuarioEmpresa = criarUsuarioEmpresa();

        when(service.cadastrar(
                any(UsuarioEmpresaRecord.class)
        )).thenReturn(usuarioEmpresa);

        mockMvc.perform(
                        post("/acesso/usuario-empresa")
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
                                "http://localhost/acesso/"
                                        + "usuario-empresa/3"
                        )
                )
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(
                        jsonPath("$.idUsuario").value(1L)
                )
                .andExpect(
                        jsonPath("$.usuario")
                                .value("usuario@teste.com")
                )
                .andExpect(
                        jsonPath("$.idEmpresa").value(2L)
                )
                .andExpect(
                        jsonPath("$.empresa")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.todasSubsidiarias")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.status").value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve rejeitar cadastro invalido")
    void deveRejeitarCadastroInvalido()
            throws Exception {
        var dados = new UsuarioEmpresaRecord(
                null,
                null,
                null
        );

        mockMvc.perform(
                        post("/acesso/usuario-empresa")
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
    @DisplayName("Deve listar vinculos")
    void deveListarVinculos() throws Exception {
        var lista = List.of(
                new ListaUsuarioEmpresaRecord(
                        3L,
                        1L,
                        "usuario@teste.com",
                        2L,
                        "Empresa Exemplo",
                        true,
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
                        get("/acesso/usuario-empresa")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath("$.content[0].idUsuario")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.content[0].usuario")
                                .value("usuario@teste.com")
                )
                .andExpect(
                        jsonPath("$.content[0].idEmpresa")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.content[0].empresa")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].todasSubsidiarias"
                        ).value(true)
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
    @DisplayName("Deve listar por usuario e empresa")
    void deveListarPorUsuarioEEmpresa()
            throws Exception {
        var pagina =
                new PageImpl<ListaUsuarioEmpresaRecord>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(service.listar(
                any(Pageable.class),
                eq(1L),
                eq(2L)
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/acesso/usuario-empresa")
                                .param("idUsuario", "1")
                                .param("idEmpresa", "2")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.content").isEmpty()
                );

        verify(service).listar(
                any(Pageable.class),
                eq(1L),
                eq(2L)
        );
    }

    @Test
    @DisplayName("Deve detalhar vinculo")
    void deveDetalharVinculo() throws Exception {
        var detalhe = new DetalheUsuarioEmpresaRecord(
                3L,
                1L,
                "usuario@teste.com",
                2L,
                "Empresa Exemplo",
                true,
                StatusEnum.ATIVO
        );

        when(service.detalhar(3L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        get("/acesso/usuario-empresa/3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(
                        jsonPath("$.idUsuario").value(1L)
                )
                .andExpect(
                        jsonPath("$.usuario")
                                .value("usuario@teste.com")
                )
                .andExpect(
                        jsonPath("$.idEmpresa").value(2L)
                )
                .andExpect(
                        jsonPath("$.empresa")
                                .value("Empresa Exemplo")
                )
                .andExpect(
                        jsonPath("$.todasSubsidiarias")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.status").value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve atualizar vinculo")
    void deveAtualizarVinculo() throws Exception {
        var dados = new AtualizaUsuarioEmpresaRecord(
                3L,
                false
        );

        var detalhe = new DetalheUsuarioEmpresaRecord(
                3L,
                1L,
                "usuario@teste.com",
                2L,
                "Empresa Exemplo",
                false,
                StatusEnum.ATIVO
        );

        when(service.atualizar(
                any(AtualizaUsuarioEmpresaRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                        put("/acesso/usuario-empresa")
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
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(
                        jsonPath("$.todasSubsidiarias")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.status").value("ATIVO")
                );
    }

    @Test
    @DisplayName("Deve rejeitar atualizacao invalida")
    void deveRejeitarAtualizacaoInvalida()
            throws Exception {
        var dados = new AtualizaUsuarioEmpresaRecord(
                null,
                null
        );

        mockMvc.perform(
                        put("/acesso/usuario-empresa")
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
    @DisplayName("Deve excluir vinculo")
    void deveExcluirVinculo() throws Exception {
        mockMvc.perform(
                        delete("/acesso/usuario-empresa/3")
                )
                .andExpect(status().isNoContent());

        verify(service).excluir(3L);
    }

    private UsuarioEmpresaModel criarUsuarioEmpresa() {
        var usuario = new UsuarioModel(
                new UsuarioRecord(
                        "usuario@teste.com",
                        "123456"
                ),
                "senha-criptografada"
        );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                1L
        );

        var empresa = new EmpresaModel(
                new EmpresaRecord("Empresa Exemplo")
        );

        ReflectionTestUtils.setField(
                empresa,
                "id",
                2L
        );

        var usuarioEmpresa = new UsuarioEmpresaModel(
                usuario,
                empresa,
                true
        );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        return usuarioEmpresa;
    }
}