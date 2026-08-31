package com.empresa.erp.domain.plataforma.organizacao.convite.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import com.empresa.erp.domain.plataforma.organizacao.convite.model.StatusConviteOrganizacaoEnum;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoNovoUsuarioRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.AceiteConviteOrganizacaoUsuarioExistenteRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConsultaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.DetalheConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ListaConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.record.ResultadoAceiteConviteOrganizacaoRecord;
import com.empresa.erp.domain.plataforma.organizacao.convite.service.ConviteOrganizacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;

class ConviteOrganizacaoControllerTest {

    private static final LocalDateTime CRIADO_EM =
            LocalDateTime.of(
                    2026,
                    8,
                    23,
                    10,
                    0
            );

    private static final LocalDateTime EXPIRA_EM =
            LocalDateTime.of(
                    2026,
                    8,
                    25,
                    10,
                    0
            );

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ConviteOrganizacaoService service;

    @BeforeEach
    void setUp() {
        service = org.mockito.Mockito.mock(
                ConviteOrganizacaoService.class
        );

        objectMapper =
                new ObjectMapper()
                        .findAndRegisterModules();

        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new ConviteOrganizacaoController(
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
            "Deve criar convite"
    )
    void deveCriarConvite()
            throws Exception {
        var dados =
                new ConviteOrganizacaoRecord(
                        "Organização Principal",
                        "admin@teste.com"
                );

        var detalhe =
                criarDetalhePendente(10L);

        when(service.convidar(
                any(ConviteOrganizacaoRecord.class)
        )).thenReturn(detalhe);

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/convite"
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
                        status().isCreated()
                )
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/"
                                        + "plataforma/organizacao/"
                                        + "convite/10"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.nomeOrganizacao")
                                .value(
                                        "Organização Principal"
                                )
                )
                .andExpect(
                        jsonPath("$.emailAdministrador")
                                .value(
                                        "admin@teste.com"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDENTE")
                )
                .andExpect(
                        jsonPath("$.expirado")
                                .value(false)
                );

        verify(service).convidar(
                any(ConviteOrganizacaoRecord.class)
        );
    }

    @Test
    @DisplayName(
            "Deve rejeitar criação com nome em branco"
    )
    void deveRejeitarCriacaoComNomeEmBranco()
            throws Exception {
        var dados =
                new ConviteOrganizacaoRecord(
                        "",
                        "admin@teste.com"
                );

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/convite"
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
            "Deve rejeitar criação com e-mail inválido"
    )
    void deveRejeitarCriacaoComEmailInvalido()
            throws Exception {
        var dados =
                new ConviteOrganizacaoRecord(
                        "Organização",
                        "email-invalido"
                );

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/convite"
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
            "Deve listar convites"
    )
    void deveListarConvites()
            throws Exception {
        var convite =
                new ListaConviteOrganizacaoRecord(
                        10L,
                        "Organização Principal",
                        "admin@teste.com",
                        CRIADO_EM,
                        EXPIRA_EM,
                        StatusConviteOrganizacaoEnum.PENDENTE,
                        false
                );

        var pagina =
                new PageImpl<>(
                        List.of(convite),
                        PageRequest.of(0, 10),
                        1
                );

        when(service.listar(
                any(Pageable.class),
                isNull(),
                isNull()
        )).thenReturn(pagina);

        mockMvc.perform(
                        get(
                                "/plataforma/organizacao/convite"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content[0].id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].nomeOrganizacao"
                        ).value(
                                "Organização Principal"
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.content[0].emailAdministrador"
                        ).value(
                                "admin@teste.com"
                        )
                )
                .andExpect(
                        jsonPath("$.content[0].status")
                                .value("PENDENTE")
                )
                .andExpect(
                        jsonPath("$.content[0].expirado")
                                .value(false)
                )
                .andExpect(
                        jsonPath("$.totalElements")
                                .value(1)
                );

        verify(service).listar(
                any(Pageable.class),
                isNull(),
                isNull()
        );
    }

    @Test
    @DisplayName(
            "Deve listar convites com filtro e status"
    )
    void deveListarConvitesComFiltroEStatus()
            throws Exception {
        var pagina =
                new PageImpl<ListaConviteOrganizacaoRecord>(
                        List.of(),
                        PageRequest.of(0, 10),
                        0
                );

        when(service.listar(
                any(Pageable.class),
                eq("Principal"),
                eq(
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        )).thenReturn(pagina);

        mockMvc.perform(
                        get(
                                "/plataforma/organizacao/convite"
                        )
                                .param(
                                        "filtro",
                                        "Principal"
                                )
                                .param(
                                        "status",
                                        "PENDENTE"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.content")
                                .isEmpty()
                );

        verify(service).listar(
                any(Pageable.class),
                eq("Principal"),
                eq(
                        StatusConviteOrganizacaoEnum.PENDENTE
                )
        );
    }

    @Test
    @DisplayName(
            "Deve detalhar convite"
    )
    void deveDetalharConvite()
            throws Exception {
        when(service.detalhar(10L))
                .thenReturn(
                        criarDetalhePendente(10L)
                );

        mockMvc.perform(
                        get(
                                "/plataforma/organizacao/"
                                        + "convite/10"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.nomeOrganizacao")
                                .value(
                                        "Organização Principal"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDENTE")
                )
                .andExpect(
                        jsonPath("$.expirado")
                                .value(false)
                );

        verify(service).detalhar(10L);
    }

    @Test
    @DisplayName(
            "Deve revogar convite"
    )
    void deveRevogarConvite()
            throws Exception {
        mockMvc.perform(
                        delete(
                                "/plataforma/organizacao/"
                                        + "convite/10"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(service).revogar(10L);
    }

    @Test
    @DisplayName(
            "Deve reenviar convite"
    )
    void deveReenviarConvite()
            throws Exception {
        when(service.reenviar(10L))
                .thenReturn(
                        criarDetalhePendente(10L)
                );

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/10/reenvio"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDENTE")
                )
                .andExpect(
                        jsonPath("$.expirado")
                                .value(false)
                );

        verify(service).reenviar(10L);
    }

    @Test
    @DisplayName(
            "Deve consultar convite"
    )
    void deveConsultarConvite()
            throws Exception {
        var consulta =
                new ConsultaConviteOrganizacaoRecord(
                        "Organização Principal",
                        "a***@teste.com",
                        true
                );

        when(service.consultar("token-convite"))
                .thenReturn(consulta);

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/consulta"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": "token-convite"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.nomeOrganizacao")
                                .value(
                                        "Organização Principal"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.emailAdministradorMascarado"
                        ).value(
                                "a***@teste.com"
                        )
                )
                .andExpect(
                        jsonPath("$.usuarioExistente")
                                .value(true)
                );

        verify(service).consultar(
                "token-convite"
        );
    }

    @Test
    @DisplayName(
            "Deve rejeitar consulta sem token"
    )
    void deveRejeitarConsultaSemToken()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/consulta"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": ""
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName(
            "Deve aceitar convite com usuário existente"
    )
    void deveAceitarConviteComUsuarioExistente()
            throws Exception {
        var resultado =
                new ResultadoAceiteConviteOrganizacaoRecord(
                        30L,
                        "Organização Principal"
                );

        when(service.aceitarUsuarioExistente(
                any(
                        AceiteConviteOrganizacaoUsuarioExistenteRecord.class
                )
        )).thenReturn(resultado);

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/aceite/"
                                        + "usuario-existente"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": "token-convite"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.idOrganizacao")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$.nomeOrganizacao")
                                .value(
                                        "Organização Principal"
                                )
                );

        verify(service).aceitarUsuarioExistente(
                any(
                        AceiteConviteOrganizacaoUsuarioExistenteRecord.class
                )
        );
    }

    @Test
    @DisplayName(
            "Deve rejeitar aceite de usuário existente sem token"
    )
    void deveRejeitarAceiteUsuarioExistenteSemToken()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/aceite/"
                                        + "usuario-existente"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": ""
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName(
            "Deve aceitar convite criando novo usuário"
    )
    void deveAceitarConviteCriandoNovoUsuario()
            throws Exception {
        var resultado =
                new ResultadoAceiteConviteOrganizacaoRecord(
                        30L,
                        "Organização Principal"
                );

        when(service.aceitarNovoUsuario(
                any(
                        AceiteConviteOrganizacaoNovoUsuarioRecord.class
                )
        )).thenReturn(resultado);

        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/aceite/"
                                        + "novo-usuario"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": "token-convite",
                                          "senha": "Senha@123"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.idOrganizacao")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$.nomeOrganizacao")
                                .value(
                                        "Organização Principal"
                                )
                );

        verify(service).aceitarNovoUsuario(
                any(
                        AceiteConviteOrganizacaoNovoUsuarioRecord.class
                )
        );
    }

    @Test
    @DisplayName(
            "Deve rejeitar aceite de novo usuário sem token"
    )
    void deveRejeitarAceiteNovoUsuarioSemToken()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/aceite/"
                                        + "novo-usuario"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": "",
                                          "senha": "Senha@123"
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName(
            "Deve rejeitar aceite de novo usuário sem senha"
    )
    void deveRejeitarAceiteNovoUsuarioSemSenha()
            throws Exception {
        mockMvc.perform(
                        post(
                                "/plataforma/organizacao/"
                                        + "convite/aceite/"
                                        + "novo-usuario"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        """
                                        {
                                          "token": "token-convite",
                                          "senha": ""
                                        }
                                        """
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verifyNoInteractions(service);
    }

    private DetalheConviteOrganizacaoRecord
            criarDetalhePendente(
                    Long id
            ) {
        return new DetalheConviteOrganizacaoRecord(
                id,
                "Organização Principal",
                "admin@teste.com",
                CRIADO_EM,
                EXPIRA_EM,
                null,
                StatusConviteOrganizacaoEnum.PENDENTE,
                false
        );
    }
}