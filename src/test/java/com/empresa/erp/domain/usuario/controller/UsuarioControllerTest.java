package com.empresa.erp.domain.usuario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.DetalheUsuarioRecord;
import com.empresa.erp.domain.usuario.record.ListaUsuarioRecord;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.empresa.erp.domain.usuario.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

class UsuarioControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private UsuarioService service;

    @BeforeEach
    void setUp() {
        service =
                org.mockito.Mockito.mock(
                        UsuarioService.class
                );

        objectMapper =
                new ObjectMapper();

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new UsuarioController(
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
            "Deve cadastrar usuário e retornar status 201"
    )
    void deveCadastrarUsuarioERetornarStatus201()
            throws Exception {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "usuario@teste.com",
                        "Senha@123"
                );

        UsuarioOrganizacaoModel vinculo =
                criarVinculo(
                        1L,
                        "usuario@teste.com"
                );

        when(service.cadastrar(
                any(UsuarioRecord.class)
        )).thenReturn(vinculo);

        mockMvc.perform(
                post("/usuario")
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
                                "http://localhost/usuario/1"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(
                                        "usuario@teste.com"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .cadastrar(
                        any(UsuarioRecord.class)
                );
    }

    @Test
    @DisplayName(
            "Deve retornar 400 ao cadastrar usuário com e-mail em branco"
    )
    void deveRetornar400AoCadastrarUsuarioComEmailEmBranco()
            throws Exception {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "",
                        "Senha@123"
                );

        mockMvc.perform(
                post("/usuario")
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
            "Deve retornar 400 ao cadastrar usuário com e-mail inválido"
    )
    void deveRetornar400AoCadastrarUsuarioComEmailInvalido()
            throws Exception {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "email-invalido",
                        "Senha@123"
                );

        mockMvc.perform(
                post("/usuario")
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
            "Deve retornar 400 ao cadastrar usuário com senha em branco"
    )
    void deveRetornar400AoCadastrarUsuarioComSenhaEmBranco()
            throws Exception {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "usuario@teste.com",
                        ""
                );

        mockMvc.perform(
                post("/usuario")
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
            "Deve retornar 400 ao cadastrar usuário com senha fraca"
    )
    void deveRetornar400AoCadastrarUsuarioComSenhaFraca()
            throws Exception {
        UsuarioRecord dados =
                new UsuarioRecord(
                        "usuario@teste.com",
                        "senha"
                );

        mockMvc.perform(
                post("/usuario")
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
            "Deve listar usuários ativos"
    )
    void deveListarUsuariosAtivos()
            throws Exception {
        var lista =
                List.of(
                        new ListaUsuarioRecord(
                                1L,
                                "usuario@teste.com",
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
                isNull(),
                isNull()
        )).thenReturn(pagina);

        mockMvc.perform(
                get("/usuario")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.content[0].email")
                                .value(
                                        "usuario@teste.com"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("ATIVO")
                );

        verify(service)
                .listar(
                        any(Pageable.class),
                        isNull(),
                        isNull()
                );
    }

    @Test
    @DisplayName(
            "Deve listar usuários inativos com filtro"
    )
    void deveListarUsuariosInativosComFiltro()
            throws Exception {
        var lista =
                List.of(
                        new ListaUsuarioRecord(
                                2L,
                                "financeiro@teste.com",
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
                eq("fin"),
                eq(StatusEnum.INATIVO)
        )).thenReturn(pagina);

        mockMvc.perform(
                get("/usuario")
                        .param("filtro", "fin")
                        .param(
                                "status",
                                "INATIVO"
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
                        jsonPath("$.content[0].email")
                                .value(
                                        "financeiro@teste.com"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("INATIVO")
                );

        verify(service)
                .listar(
                        any(Pageable.class),
                        eq("fin"),
                        eq(StatusEnum.INATIVO)
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar usuário"
    )
    void deveDetalharUsuario()
            throws Exception {
        var detalhe =
                new DetalheUsuarioRecord(
                        1L,
                        "usuario@teste.com",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(1L))
                .thenReturn(detalhe);

        mockMvc.perform(
                get("/usuario/1")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(
                                        "usuario@teste.com"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .detalhar(1L);
    }

    @Test
    @DisplayName(
            "Deve inativar vínculo do usuário e retornar status 204"
    )
    void deveInativarVinculoDoUsuarioERetornarStatus204()
            throws Exception {
        mockMvc.perform(
                delete("/usuario/1")
        )
                .andExpect(
                        status().isNoContent()
                );

        verify(service)
                .excluir(1L);
    }

    @Test
    @DisplayName(
            "Deve reativar vínculo do usuário e retornar status 200"
    )
    void deveReativarVinculoDoUsuarioERetornarStatus200()
            throws Exception {
        var detalhe =
                new DetalheUsuarioRecord(
                        1L,
                        "usuario@teste.com",
                        StatusEnum.ATIVO
                );

        when(service.reativar(1L))
                .thenReturn(detalhe);

        mockMvc.perform(
                patch("/usuario/1/reativar")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value(
                                        "usuario@teste.com"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .reativar(1L);
    }

    private UsuarioOrganizacaoModel criarVinculo(
            Long idUsuario,
            String email
    ) {
        UsuarioModel usuario =
                new UsuarioModel(
                        idUsuario,
                        email,
                        "senha-criptografada",
                        StatusEnum.ATIVO
                );

        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        "Organização Principal"
                );

        return new UsuarioOrganizacaoModel(
                usuario,
                organizacao
        );
    }
}