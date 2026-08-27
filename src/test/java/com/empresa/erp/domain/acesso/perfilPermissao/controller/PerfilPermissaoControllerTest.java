package com.empresa.erp.domain.acesso.perfilPermissao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;
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

import com.empresa.erp.domain.acesso.perfil.model.PerfilModel;
import com.empresa.erp.domain.acesso.perfilPermissao.model.PerfilPermissaoModel;
import com.empresa.erp.domain.acesso.perfilPermissao.record.DetalhePerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.ListaPerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.record.PerfilPermissaoRecord;
import com.empresa.erp.domain.acesso.perfilPermissao.service.PerfilPermissaoService;
import com.empresa.erp.domain.acesso.permissao.model.EscopoPermissaoEnum;
import com.empresa.erp.domain.acesso.permissao.model.PermissaoModel;
import com.empresa.erp.domain.base.model.StatusEnum;
import com.empresa.erp.domain.organizacao.model.OrganizacaoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

class PerfilPermissaoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private PerfilPermissaoService service;

    @BeforeEach
    void setUp() {
        service =
                org.mockito.Mockito.mock(
                        PerfilPermissaoService.class
                );

        objectMapper =
                new ObjectMapper();

        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new PerfilPermissaoController(
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
            "Deve vincular permissão ao perfil e retornar status 201"
    )
    void deveVincularPermissaoAoPerfilERetornarStatus201()
            throws Exception {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        2L
                );

        PerfilModel perfil =
                criarPerfil(
                        1L,
                        "Administrador",
                        "Perfil administrador"
                );

        PermissaoModel permissao =
                criarPermissao(
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR",
                        "Permite listar perfis"
                );

        PerfilPermissaoModel perfilPermissao =
                criarPerfilPermissao(
                        3L,
                        perfil,
                        permissao
                );

        when(service.cadastrar(
                any(PerfilPermissaoRecord.class)
        )).thenReturn(perfilPermissao);

        mockMvc.perform(
                post("/perfil-permissao")
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
                                "http://localhost/perfil-permissao/3"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath("$.idPerfil")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.perfil")
                                .value("Administrador")
                )
                .andExpect(
                        jsonPath("$.idPermissao")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.permissao")
                                .value("Listar perfis")
                )
                .andExpect(
                        jsonPath("$.chave")
                                .value(
                                        "ACESSO_PERFIL_LISTAR"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ATIVO")
                );

        verify(service)
                .cadastrar(
                        any(PerfilPermissaoRecord.class)
                );
    }

    @Test
    @DisplayName(
            "Deve retornar 400 ao vincular permissão sem perfil"
    )
    void deveRetornar400AoVincularPermissaoSemPerfil()
            throws Exception {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        null,
                        2L
                );

        mockMvc.perform(
                post("/perfil-permissao")
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
            "Deve retornar 400 ao vincular permissão sem permissão"
    )
    void deveRetornar400AoVincularPermissaoSemPermissao()
            throws Exception {
        PerfilPermissaoRecord dados =
                new PerfilPermissaoRecord(
                        1L,
                        null
                );

        mockMvc.perform(
                post("/perfil-permissao")
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
            "Deve listar permissões por perfil"
    )
    void deveListarPermissoesPorPerfil()
            throws Exception {
        var lista =
                List.of(
                        new ListaPerfilPermissaoRecord(
                                3L,
                                2L,
                                "Listar perfis",
                                "ACESSO_PERFIL_LISTAR",
                                StatusEnum.ATIVO
                        )
                );

        var pagina =
                new PageImpl<>(
                        lista,
                        PageRequest.of(0, 10),
                        lista.size()
                );

        when(service.listarPorPerfil(
                any(Pageable.class),
                eq(1L)
        )).thenReturn(pagina);

        mockMvc.perform(
                get(
                        "/perfil-permissao/perfil/1"
                )
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].idPermissao"
                        ).value(2L)
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].permissao"
                        ).value("Listar perfis")
                )
                .andExpect(
                        jsonPath("$.content[0].chave")
                                .value(
                                        "ACESSO_PERFIL_LISTAR"
                                )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("ATIVO")
                );

        verify(service)
                .listarPorPerfil(
                        any(Pageable.class),
                        eq(1L)
                );
    }

    @Test
    @DisplayName(
            "Deve detalhar vínculo entre perfil e permissão"
    )
    void deveDetalharVinculoEntrePerfilEPermissao()
            throws Exception {
        var detalhe =
                new DetalhePerfilPermissaoRecord(
                        3L,
                        1L,
                        "Administrador",
                        2L,
                        "Listar perfis",
                        "ACESSO_PERFIL_LISTAR",
                        StatusEnum.ATIVO
                );

        when(service.detalhar(3L))
                .thenReturn(detalhe);

        mockMvc.perform(
                get("/perfil-permissao/3")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(3L)
                )
                .andExpect(
                        jsonPath("$.idPerfil")
                                .value(1L)
                )
                .andExpect(
                        jsonPath("$.perfil")
                                .value("Administrador")
                )
                .andExpect(
                        jsonPath("$.idPermissao")
                                .value(2L)
                )
                .andExpect(
                        jsonPath("$.permissao")
                                .value("Listar perfis")
                )
                .andExpect(
                        jsonPath("$.chave")
                                .value(
                                        "ACESSO_PERFIL_LISTAR"
                                )
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
            "Deve remover vínculo entre perfil e permissão e retornar status 204"
    )
    void deveRemoverVinculoEntrePerfilEPermissaoERetornarStatus204()
            throws Exception {
        mockMvc.perform(
                delete("/perfil-permissao/3")
        )
                .andExpect(
                        status().isNoContent()
                );

        verify(service)
                .excluir(3L);
    }

    private PerfilModel criarPerfil(
            Long id,
            String nome,
            String descricao
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
                StatusEnum.ATIVO
        );
    }

    private PermissaoModel criarPermissao(
            Long id,
            String nome,
            String chave,
            String descricao
    ) {
        PermissaoModel permissao =
                instanciarPermissao();

        ReflectionTestUtils.setField(
                permissao,
                "id",
                id
        );

        ReflectionTestUtils.setField(
                permissao,
                "nome",
                nome
        );

        ReflectionTestUtils.setField(
                permissao,
                "chave",
                chave
        );

        ReflectionTestUtils.setField(
                permissao,
                "descricao",
                descricao
        );

        ReflectionTestUtils.setField(
                permissao,
                "sistema",
                true
        );

        ReflectionTestUtils.setField(
                permissao,
                "escopo",
                EscopoPermissaoEnum.ORGANIZACAO
        );

        ReflectionTestUtils.setField(
                permissao,
                "status",
                StatusEnum.ATIVO
        );

        return permissao;
    }

    private PermissaoModel instanciarPermissao() {
        try {
            var construtor =
                    PermissaoModel.class
                            .getDeclaredConstructor();

            construtor.setAccessible(true);

            return construtor.newInstance();
        } catch (
                InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException exception
        ) {
            throw new IllegalStateException(
                    "Não foi possível criar permissão para o teste.",
                    exception
            );
        }
    }

    private PerfilPermissaoModel
            criarPerfilPermissao(
                    Long id,
                    PerfilModel perfil,
                    PermissaoModel permissao
            ) {
        PerfilPermissaoModel perfilPermissao =
                new PerfilPermissaoModel(
                        perfil,
                        permissao
                );

        ReflectionTestUtils.setField(
                perfilPermissao,
                "id",
                id
        );

        return perfilPermissao;
    }
}