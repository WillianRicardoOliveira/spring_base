package com.empresa.erp.domain.acesso.usuarioEstabelecimento.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.model.UsuarioEstabelecimentoModel;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.DetalheUsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.ListaUsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.record.UsuarioEstabelecimentoRecord;
import com.empresa.erp.domain.acesso.usuarioEstabelecimento.service.UsuarioEstabelecimentoService;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.configuracao.empresa.model.EmpresaModel;
import com.empresa.erp.domain.configuracao.empresa.record.EmpresaRecord;
import com.empresa.erp.domain.configuracao.estabelecimento.model.EstabelecimentoModel;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

class UsuarioEstabelecimentoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UsuarioEstabelecimentoService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                UsuarioEstabelecimentoService.class
        );

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new UsuarioEstabelecimentoController(
                                service
                        )
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("Deve cadastrar vinculo")
    void deveCadastrarVinculo()
            throws Exception {
        var dados = new UsuarioEstabelecimentoRecord(
                3L,
                4L
        );

        var usuarioEstabelecimento =
                criarUsuarioEstabelecimento();

        when(service.cadastrar(
                any(UsuarioEstabelecimentoRecord.class)
        )).thenReturn(usuarioEstabelecimento);

        mockMvc.perform(
                        post(
                                "/acesso/usuario-estabelecimento"
                        )
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
                                        + "usuario-estabelecimento/6"
                        )
                )
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.idUsuarioEmpresa").value(3L))
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.usuario").value("usuario@teste.com"))
                .andExpect(jsonPath("$.idEmpresa").value(2L))
                .andExpect(jsonPath("$.empresa").value("Empresa Exemplo"))
                .andExpect(jsonPath("$.idEstabelecimento").value(4L))
                .andExpect(jsonPath("$.estabelecimento").value("Filial Curitiba"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve rejeitar cadastro invalido")
    void deveRejeitarCadastroInvalido()
            throws Exception {
        var dados = new UsuarioEstabelecimentoRecord(
                null,
                null
        );

        mockMvc.perform(
                        post(
                                "/acesso/usuario-estabelecimento"
                        )
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
    void deveListarVinculos()
            throws Exception {
        var lista = List.of(
                new ListaUsuarioEstabelecimentoRecord(
                        6L,
                        3L,
                        1L,
                        "usuario@teste.com",
                        2L,
                        "Empresa Exemplo",
                        4L,
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
                isNull()
        )).thenReturn(pagina);

        mockMvc.perform(
                        get(
                                "/acesso/usuario-estabelecimento"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(6L))
                .andExpect(jsonPath("$.content[0].idUsuarioEmpresa").value(3L))
                .andExpect(jsonPath("$.content[0].idEstabelecimento").value(4L))
                .andExpect(jsonPath("$.content[0].estabelecimento").value("Filial Curitiba"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"));

        verify(service).listar(
                any(Pageable.class),
                isNull()
        );
    }

    @Test
    @DisplayName("Deve listar por usuario empresa")
    void deveListarPorUsuarioEmpresa()
            throws Exception {
        var pagina =
                new PageImpl<ListaUsuarioEstabelecimentoRecord>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(service.listar(
                any(Pageable.class),
                eq(3L)
        )).thenReturn(pagina);

        mockMvc.perform(
                        get(
                                "/acesso/usuario-estabelecimento"
                        )
                                .param(
                                        "idUsuarioEmpresa",
                                        "3"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(service).listar(
                any(Pageable.class),
                eq(3L)
        );
    }

    @Test
    @DisplayName("Deve detalhar vinculo")
    void deveDetalharVinculo()
            throws Exception {
        var detalhe =
                new DetalheUsuarioEstabelecimentoRecord(
                        6L,
                        3L,
                        1L,
                        "usuario@teste.com",
                        2L,
                        "Empresa Exemplo",
                        4L,
                        "Filial Curitiba",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(6L))
                .thenReturn(detalhe);

        mockMvc.perform(
                        get(
                                "/acesso/"
                                        + "usuario-estabelecimento/6"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.idUsuarioEmpresa").value(3L))
                .andExpect(jsonPath("$.idUsuario").value(1L))
                .andExpect(jsonPath("$.idEmpresa").value(2L))
                .andExpect(jsonPath("$.idEstabelecimento").value(4L))
                .andExpect(jsonPath("$.estabelecimento").value("Filial Curitiba"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve excluir vinculo")
    void deveExcluirVinculo()
            throws Exception {
        mockMvc.perform(
                        delete(
                                "/acesso/"
                                        + "usuario-estabelecimento/6"
                        )
                )
                .andExpect(status().isNoContent());

        verify(service).excluir(6L);
    }

    private UsuarioEstabelecimentoModel criarUsuarioEstabelecimento() {
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
                2L
        );

        var usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        ReflectionTestUtils.setField(
                usuarioOrganizacao,
                "id",
                5L
        );

        var usuarioEmpresa =
                new UsuarioEmpresaModel(
                        usuarioOrganizacao,
                        empresa,
                        false
                );

        ReflectionTestUtils.setField(
                usuarioEmpresa,
                "id",
                3L
        );

        var estabelecimento = new EstabelecimentoModel(
                empresa,
                "Filial Curitiba"
        );

        ReflectionTestUtils.setField(
                estabelecimento,
                "id",
                4L
        );

        var usuarioEstabelecimento =
                new UsuarioEstabelecimentoModel(
                        usuarioEmpresa,
                        estabelecimento
                );

        ReflectionTestUtils.setField(
                usuarioEstabelecimento,
                "id",
                6L
        );

        return usuarioEstabelecimento;
    }
}