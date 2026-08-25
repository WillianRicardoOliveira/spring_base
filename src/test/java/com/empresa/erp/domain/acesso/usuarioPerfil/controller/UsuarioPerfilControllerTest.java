package com.empresa.erp.domain.acesso.usuarioPerfil.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfil.record.PerfilRecord;
import com.empresa.erp.domain.acesso.usuarioOrganizacao.model.UsuarioOrganizacaoModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.model.UsuarioPerfilModel;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.DetalheUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.ListaUsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.record.UsuarioPerfilRecord;
import com.empresa.erp.domain.acesso.usuarioPerfil.service.UsuarioPerfilService;
import com.empresa.erp.domain.old.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.empresa.erp.domain.usuario.model.UsuarioModel;
import com.empresa.erp.domain.usuario.record.UsuarioRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

class UsuarioPerfilControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private UsuarioPerfilService service;

    @BeforeEach
    void setUp() {
        service =
                org.mockito.Mockito.mock(
                        UsuarioPerfilService.class
                );

        objectMapper =
                new ObjectMapper();

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new UsuarioPerfilController(
                                        service
                                )
                        )
                        .build();
    }

    @Test
    @DisplayName(
            "Deve vincular perfil ao usuário e retornar status 201"
    )
    void deveVincularPerfilAoUsuarioERetornarStatus201()
            throws Exception {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        1L,
                        2L
                );

        OrganizacaoModel organizacao =
                criarOrganizacao(
                        10L,
                        "Organização Principal"
                );

        UsuarioModel usuario =
                criarUsuario(
                        1L,
                        "usuario@teste.com"
                );

        UsuarioOrganizacaoModel usuarioOrganizacao =
                criarUsuarioOrganizacao(
                        20L,
                        usuario,
                        organizacao
                );

        PerfilModel perfil =
                criarPerfil(
                        2L,
                        organizacao,
                        "Administrador",
                        "Perfil administrador"
                );

        UsuarioPerfilModel usuarioPerfil =
                criarUsuarioPerfil(
                        3L,
                        usuarioOrganizacao,
                        perfil
                );

        when(service.cadastrar(
                any(UsuarioPerfilRecord.class)
        )).thenReturn(usuarioPerfil);

        mockMvc.perform(
                post("/usuario-perfil")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
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
                                "http://localhost/usuario-perfil/3"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath("$.idUsuario")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.usuario")
                                .value("usuario@teste.com")
                )
                .andExpect(
                        jsonPath("$.idPerfil")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.perfil")
                                .value("Administrador")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .cadastrar(
                        any(UsuarioPerfilRecord.class)
                );
    }

    @Test
    @DisplayName(
            "Deve retornar 400 ao vincular perfil sem usuário"
    )
    void deveRetornar400AoVincularPerfilSemUsuario()
            throws Exception {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        null,
                        2L
                );

        mockMvc.perform(
                post("/usuario-perfil")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
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
            "Deve retornar 400 ao vincular usuário sem perfil"
    )
    void deveRetornar400AoVincularUsuarioSemPerfil()
            throws Exception {
        UsuarioPerfilRecord dados =
                new UsuarioPerfilRecord(
                        1L,
                        null
                );

        mockMvc.perform(
                post("/usuario-perfil")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
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
            "Deve listar perfis por usuário"
    )
    void deveListarPerfisPorUsuario()
            throws Exception {
        List<ListaUsuarioPerfilRecord> lista =
                List.of(
                        new ListaUsuarioPerfilRecord(
                                3L,
                                2L,
                                "Administrador",
                                StatusEnum.ATIVO
                        )
                );

        when(service.listarPorUsuario(1L))
                .thenReturn(lista);

        mockMvc.perform(
                get("/usuario-perfil/usuario/1")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath("$[0].idPerfil")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$[0].perfil")
                                .value("Administrador")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("ATIVO")
                );

        verify(service)
                .listarPorUsuario(1L);
    }

    @Test
    @DisplayName(
            "Deve detalhar vínculo entre usuário e perfil"
    )
    void deveDetalharVinculoEntreUsuarioEPerfil()
            throws Exception {
        DetalheUsuarioPerfilRecord detalhe =
                new DetalheUsuarioPerfilRecord(
                        3L,
                        1L,
                        "usuario@teste.com",
                        2L,
                        "Administrador",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(3L))
                .thenReturn(detalhe);

        mockMvc.perform(
                get("/usuario-perfil/3")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath("$.idUsuario")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.usuario")
                                .value("usuario@teste.com")
                )
                .andExpect(
                        jsonPath("$.idPerfil")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.perfil")
                                .value("Administrador")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .detalhar(3L);
    }

    @Test
    @DisplayName(
            "Deve remover vínculo entre usuário e perfil e retornar status 204"
    )
    void deveRemoverVinculoEntreUsuarioEPerfilERetornarStatus204()
            throws Exception {
        mockMvc.perform(
                delete("/usuario-perfil/3")
        )
                .andExpect(
                        status().isNoContent()
                );

        verify(service)
                .excluir(3L);
    }

    private OrganizacaoModel criarOrganizacao(
            Long id,
            String nome
    ) {
        OrganizacaoModel organizacao =
                new OrganizacaoModel(
                        nome
                );

        ReflectionTestUtils.setField(
                organizacao,
                "id",
                id
        );

        return organizacao;
    }

    private UsuarioModel criarUsuario(
            Long id,
            String email
    ) {
        UsuarioModel usuario =
                new UsuarioModel(
                        new UsuarioRecord(
                                email,
                                "123456"
                        ),
                        "senha-criptografada"
                );

        ReflectionTestUtils.setField(
                usuario,
                "id",
                id
        );

        return usuario;
    }

    private UsuarioOrganizacaoModel
            criarUsuarioOrganizacao(
                    Long id,
                    UsuarioModel usuario,
                    OrganizacaoModel organizacao
            ) {
        UsuarioOrganizacaoModel usuarioOrganizacao =
                new UsuarioOrganizacaoModel(
                        usuario,
                        organizacao
                );

        ReflectionTestUtils.setField(
                usuarioOrganizacao,
                "id",
                id
        );

        return usuarioOrganizacao;
    }

    private PerfilModel criarPerfil(
            Long id,
            OrganizacaoModel organizacao,
            String nome,
            String descricao
    ) {
        PerfilModel perfil =
                new PerfilModel(
                        organizacao,
                        new PerfilRecord(
                                nome,
                                descricao
                        )
                );

        ReflectionTestUtils.setField(
                perfil,
                "id",
                id
        );

        return perfil;
    }

    private UsuarioPerfilModel criarUsuarioPerfil(
            Long id,
            UsuarioOrganizacaoModel usuarioOrganizacao,
            PerfilModel perfil
    ) {
        UsuarioPerfilModel usuarioPerfil =
                new UsuarioPerfilModel(
                        usuarioOrganizacao,
                        perfil
                );

        ReflectionTestUtils.setField(
                usuarioPerfil,
                "id",
                id
        );

        return usuarioPerfil;
    }
}